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
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.createExpressionByPattern
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class ConvertToDecimalIntention : SelfTargetingOffsetIndependentIntention<KtConstantExpression>(KtConstantExpression::class.java, "Convert to decimal") {
    override fun applyTo(element: KtConstantExpression, editor: Editor?) {
        val elementType = element.getType(element.analyze()) ?: return
        val factory = KtPsiFactory(element)
        val newExpression = when {
            KotlinBuiltIns.isInt(elementType) -> factory.createExpression(element.text.parseInt()?.toString() ?: return)
            KotlinBuiltIns.isLong(elementType) -> factory.createExpressionByPattern("$0L", element.text.parseLong()?.toString() ?: return)
            else -> return
        }
        element.replaced(newExpression)
    }

    override fun isApplicableTo(element: KtConstantExpression): Boolean {
        if (!element.text.hasBinaryPrefix() && !element.text.hasHexPrefix()) return false
        val elementType = element.getType(element.analyze()) ?: return false
        return KotlinBuiltIns.isInt(elementType) || KotlinBuiltIns.isLong(elementType)
    }
}