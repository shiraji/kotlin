/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

class AddLabeledReturnInLambdaIntention :
        SelfTargetingRangeIntention<KtLambdaExpression>(KtLambdaExpression::class.java, "Add labeled return to last expression in a lambda"),
        LowPriorityAction {
    override fun applicabilityRange(element: KtLambdaExpression): TextRange? {
        if (element.getNonStrictParentOfType<KtReturnExpression>() != null) return null
        val labelName = createLabelName(element) ?: return null
        text = "Add return@$labelName"
        val lastStatement = element.bodyExpression?.statements?.last() ?: return null
        return lastStatement.textRange
    }

    override fun applyTo(element: KtLambdaExpression, editor: Editor?) {
        val labelName = createLabelName(element) ?: return
        val lastStatement = element.bodyExpression?.statements?.last() ?: return
        val newExpression = KtPsiFactory(element.project).createExpression("return@$labelName ${lastStatement.text}")
        lastStatement.replace(newExpression)
    }

    private fun createLabelName(element: KtLambdaExpression): String? {
        val block = element.bodyExpression ?: return null
        val callExpression = element.getStrictParentOfType<KtCallExpression>() ?: return null
        val index = callExpression.valueArguments.indexOfFirst {
            val argumentExpression = it.getArgumentExpression()
            val lambda = when(argumentExpression) {
                is KtLambdaExpression -> argumentExpression
                is KtLabeledExpression -> argumentExpression.getChildOfType() ?: return null
                else -> return null
            }
            lambda.bodyExpression == block
        }
        if (index < 0) return null
        val resolvedCall = callExpression.getResolvedCall(callExpression.analyze()) ?: return null
        if (resolvedCall.candidateDescriptor.valueParameters[index].type.arguments.last().type.isUnit()) return null
        val lambdaLabelName = (callExpression.valueArguments[index].getArgumentExpression() as? KtLabeledExpression)?.getLabelName()
        return if (lambdaLabelName == null) callExpression.getCallNameExpression()?.text else lambdaLabelName
    }

}