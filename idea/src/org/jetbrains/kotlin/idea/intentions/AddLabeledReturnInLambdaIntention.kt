/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

class AddLabeledReturnInLambdaIntention :
        SelfTargetingIntention<KtExpression>(KtExpression::class.java, "Add labeled return to last expression in a lambda"),
        LowPriorityAction {
    override fun isApplicableTo(element: KtExpression, caretOffset: Int): Boolean {
        if (element.getNonStrictParentOfType<KtReturnExpression>() != null) return false
        val labelName = createLabelName(element) ?: return false
        text = "Add return@$labelName"
        return true
    }

    override fun applyTo(element: KtExpression, editor: Editor?) {
        val labelName = createLabelName(element) ?: return
        val newExpression = KtPsiFactory(element.project).createExpression("return@$labelName ${element.text}")
        element.replace(newExpression)
    }

    private fun createLabelName(element: KtExpression): String? {
        val block = element.getStrictParentOfType<KtBlockExpression>() ?: return null
        if (block.statements.lastOrNull() != element) return null
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