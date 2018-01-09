/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class RemoveLabeledReturnInLambdaIntention :
        SelfTargetingIntention<KtReturnExpression>(KtReturnExpression::class.java, "Remove labeled return to last expression in a lambda"),
        LowPriorityAction {
    override fun isApplicableTo(element: KtReturnExpression, caretOffset: Int): Boolean {
        val labelName = element.getLabelName() ?: return false
        val callExpression = element.getStrictParentOfType<KtCallExpression>() ?: return false
        val callName = callExpression.getCallNameExpression()?.text ?: return false
        if (labelName != callName) return false
        val block = element.getStrictParentOfType<KtBlockExpression>() ?: return false
        if (block.statements.lastOrNull() != element) return false
        text = "Remove return@$labelName"
        return true
    }

    override fun applyTo(element: KtReturnExpression, editor: Editor?) {
        val returnedExpression = element.returnedExpression
        if (returnedExpression == null)
            element.delete()
        else
            element.replace(returnedExpression)
    }
}