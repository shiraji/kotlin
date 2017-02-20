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
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespaceAndComments
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespaceAndComments

class ConvertToApplyIntention : SelfTargetingIntention<KtExpression>(KtExpression::class.java, "Convert to apply") {
    private val BLACKLIST_RECEIVER_NAME = listOf("this", "it")

    override fun isApplicableTo(element: KtExpression, caretOffset: Int): Boolean {
        return when (element) {
            is KtProperty -> element.isApplicable()
            is KtDotQualifiedExpression -> {
                val receiverExpressionText = element.getLeftMostReceiverExpression().text
                element.isApplicable(receiverExpressionText) && element.findTargetProperty(receiverExpressionText)?.isApplicable() ?: false
            }
            else -> false
        }
    }

    private fun KtProperty.isApplicable(): Boolean {
        if (!isLocal) return false
        val nextSibling = getNextSiblingIgnoringWhitespaceAndComments(false) as? KtDotQualifiedExpression
        val localVariableName = name ?: return false
        return nextSibling != null && nextSibling.isApplicable(localVariableName)
    }

    private fun KtDotQualifiedExpression.findTargetProperty(receiverExpressionText: String): KtProperty? {
        val target = getPrevSiblingIgnoringWhitespaceAndComments(false)
        when (target) {
            is KtProperty -> if (target.name == receiverExpressionText) return target
            is KtDotQualifiedExpression -> if (target.isApplicable(receiverExpressionText)) return target.findTargetProperty(receiverExpressionText)
        }
        return null
    }

    private fun KtProperty.findApplicableLastExpression(receiverExpressionText: String): PsiElement {
        var targetElement: PsiElement = this
        while (true) {
            val sibling = targetElement.getNextSiblingIgnoringWhitespaceAndComments(false)
            if (sibling !is KtDotQualifiedExpression || !sibling.isApplicable(receiverExpressionText)) return targetElement
            targetElement = sibling
        }
    }

    private fun KtDotQualifiedExpression.isApplicable(receiverExpressionText: String): Boolean {
        if (receiverExpressionText != getLeftMostReceiverExpression().text) return false
        val callExpression = callExpression ?: return false
        if (!callExpression.isApplicable()) return false
        val receiverExpression = receiverExpression
        if (receiverExpression is KtDotQualifiedExpression) return receiverExpression.isApplicable(receiverExpressionText)
        return true
    }

    private fun KtCallExpression.isApplicable(): Boolean {
        when {
            lambdaArguments.isNotEmpty() -> return false
            valueArguments.firstOrNull { BLACKLIST_RECEIVER_NAME.contains(it.text) } != null -> return false
            else -> return true
        }
    }

    override fun applyTo(element: KtExpression, editor: Editor?) {
        when (element) {
            is KtProperty -> element.applyTo()
            is KtDotQualifiedExpression -> element.findTargetProperty(element.getLeftMostReceiverExpression().text)?.applyTo()
        }
    }

    private fun KtProperty.applyTo() {
        val receiverExpressionText = name ?: return
        val factory = KtPsiFactory(this)
        val property = factory.createProperty(receiverExpressionText, typeReference?.text, isVar, "${initializer?.text}.apply{}")
        val lambdaArguments = (property.initializer as? KtQualifiedExpression)?.callExpression?.lambdaArguments ?: return
        val blockExpression = lambdaArguments[0].getLambdaExpression().bodyExpression ?: return
        val parent = parent
        val lastExpression = findApplicableLastExpression(receiverExpressionText)
        blockExpression.addRange(this.nextSibling, lastExpression)
        blockExpression.children
                .map { it as? KtDotQualifiedExpression }
                .filterNotNull()
                .forEach { it.deleteFirstReceiver() }
        parent.addBefore(property, this)
        parent.deleteChildRange(this, lastExpression)
    }

}