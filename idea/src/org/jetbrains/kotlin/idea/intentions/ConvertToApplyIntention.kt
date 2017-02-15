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
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespaceAndComments

class ConvertToApplyIntention : SelfTargetingIntention<KtDotQualifiedExpression>(KtDotQualifiedExpression::class.java, "Convert to apply") {
    private val BLACKLIST_RECEIVER_NAME = listOf("this", "it")

    override fun isApplicableTo(element: KtDotQualifiedExpression, caretOffset: Int): Boolean {
        val receiverExpressionText = element.getLeftMostReceiverExpression().text
        if (BLACKLIST_RECEIVER_NAME.contains(receiverExpressionText)) return false
        val prevSibling = element.getPrevSiblingIgnoringWhitespaceAndComments(false)
        return prevSibling != null && prevSibling.isApplicable(receiverExpressionText)
    }

    private fun PsiElement.isApplicable(receiverExpressionText: String): Boolean {
        var targetElement = this
        while (true) {
            when (targetElement) {
                is KtDotQualifiedExpression -> {
                    if (targetElement.isApplicable(receiverExpressionText)) {
                        targetElement = targetElement.prevSibling
                    }
                    else return false
                }

                else -> return false
            }
        }
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

    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
    }
}