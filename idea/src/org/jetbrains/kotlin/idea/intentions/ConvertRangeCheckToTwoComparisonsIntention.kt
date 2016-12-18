/*
 * Copyright 2010-2016 JetBrains s.r.o.
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
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class ConvertRangeCheckToTwoComparisonsIntention : SelfTargetingOffsetIndependentIntention<KtBinaryExpression>(KtBinaryExpression::class.java, "Convert to comparisons") {
    override fun applyTo(element: KtBinaryExpression, editor: Editor?) {
        if (element.operationToken != KtTokens.IN_KEYWORD) return
        val rangeExpression = element.right as? KtBinaryExpression ?: return
        val min = rangeExpression.left?.text ?: return
        val arg = element.left?.text ?: return
        val max = rangeExpression.right?.text ?: return
        val comparisonsExpression = KtPsiFactory(element).createExpressionByPattern("$0 <= $1 && $1 <= $2", min, arg, max)
        element.replace(comparisonsExpression)
    }

    override fun isApplicableTo(element: KtBinaryExpression): Boolean {
        if (element.operationToken != KtTokens.IN_KEYWORD) return false
        // ignore for-loop. for(x in 1..2) should not be convert to for(1<=x && x<=2)
        if (element.getStrictParentOfType<KtForExpression>() != null && !element.isInContainerNode()) return false
        val rangeExpression = element.right as? KtBinaryExpression ?: return false
        if (rangeExpression.operationToken != KtTokens.RANGE) return false
        return true
    }

    private fun KtBinaryExpression.isInContainerNode(): Boolean {
        var parent = parent
        while (parent != null) {
            when (parent) {
                is KtForExpression -> return false
                is KtContainerNode, is KtContainerNodeForControlStructureBody -> return true
            }
            parent = parent.parent ?: return false
        }
        return false
    }

}