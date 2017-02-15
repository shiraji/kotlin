/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespaceAndComments
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespaceAndComments

class ConvertToApplyIntention : SelfTargetingIntention<KtExpression>(KtExpression::class.java, "Convert to apply") {
    private val BLACKLIST_RECEIVER_NAME = listOf("this", "it")

    override fun isApplicableTo(element: KtExpression, caretOffset: Int): Boolean {
        when (element) {
            is KtProperty -> return element.isApplicable()
            is KtDotQualifiedExpression -> return element.isApplicable()
            else -> return false
        }
    }

    private fun KtProperty.isApplicable(): Boolean {
        if (!isLocal) return false
        val nextSibling = getNextSiblingIgnoringWhitespaceAndComments(false) as? KtDotQualifiedExpression
        val localVariableName = name ?: return false
        return nextSibling != null && nextSibling.isApplicable(localVariableName)
    }

    private fun KtDotQualifiedExpression.isApplicable(): Boolean {
        val receiverExpressionText = getLeftMostReceiverExpression().text
        var targetElement: PsiElement = this
        while (targetElement is KtDotQualifiedExpression && targetElement.isApplicable(receiverExpressionText)) {
            targetElement = targetElement.getPrevSiblingIgnoringWhitespaceAndComments(false) ?: return false
        }
        return targetElement is KtProperty && targetElement.name == receiverExpressionText
    }

    private fun KtDotQualifiedExpression.isApplicable(receiverExpressionText: String): Boolean {
        if (receiverExpressionText != getLeftMostReceiverExpression().text) return false
        return callExpression?.isApplicable() ?: false
    }

    private fun KtCallExpression.isApplicable(): Boolean {
        when {
            lambdaArguments.isNotEmpty() -> return false
            valueArguments.firstOrNull { BLACKLIST_RECEIVER_NAME.contains(it.text) } != null -> return false
            else -> return true
        }
    }

    override fun applyTo(element: KtExpression, editor: Editor?) {
// todo ここから
        when (element) {
            is KtProperty -> return element.isApplicable()
            is KtDotQualifiedExpression -> return element.applyTo()
            else -> return
        }
    }

    private fun KtDotQualifiedExpression.applyTo() {
        val receiverExpression = getLeftMostReceiverExpression()
        val factory = KtPsiFactory(this)
        val scopeBlockExpression = factory.createExpressionByPattern("", receiverExpression).apply { add(factory.createNewLine()) }
        val callExpression = when (scopeBlockExpression) {
            is KtCallExpression -> scopeBlockExpression
            is KtQualifiedExpression -> scopeBlockExpression.callExpression
            else -> return
        }
        val lambdaArguments = callExpression?.lambdaArguments ?: return
        val blockExpression = lambdaArguments[0].getLambdaExpression().bodyExpression ?: return
        callExpression?.let { blockExpression.addAfter(it, blockExpression.lBrace) }
        val receiverExpressionText = receiverExpression.text
        val firstElement = blockExpression.addCallExpression(receiverExpressionText, this, true)
        val lastElement = blockExpression.addCallExpression(receiverExpressionText, this, false)
        val parent = parent
        val anchor = lastElement.nextSibling
        parent.deleteChildRange(firstElement, lastElement)
        parent.addBefore(scopeBlockExpression, anchor)
    }

    private fun KtBlockExpression.addCallExpression(receiverExpressionText: String, element: KtDotQualifiedExpression, forward: Boolean): PsiElement {
        var targetElement: PsiElement = element
        while (true) {
            val sibling = if (forward) targetElement.prevSibling else targetElement.nextSibling
            when (sibling) {
                is KtDotQualifiedExpression -> {
                    when {
                        sibling.isApplicable(receiverExpressionText) -> {
                            sibling.callExpression?.let { if (forward) addAfter(it, lBrace) else addBefore(it, rBrace) }
                            targetElement = sibling
                        }
                        else -> return targetElement
                    }
                }
                is PsiComment, is PsiWhiteSpace -> {
                    if (forward) addAfter(sibling, lBrace) else addBefore(sibling, rBrace)
                    targetElement = sibling
                }
                else -> return targetElement
            }
        }
    }
}