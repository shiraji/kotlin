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
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

abstract class ConvertNumeralSystemIntention(text: String) : SelfTargetingOffsetIndependentIntention<KtConstantExpression>(KtConstantExpression::class.java, text) {
    override fun applyTo(element: KtConstantExpression, editor: Editor?) {
        val elementType = element.getType(element.analyze()) ?: return
        val newExpression = when {
            KotlinBuiltIns.isInt(elementType) -> createIntegerExpression(element) ?: return
            KotlinBuiltIns.isLong(elementType) -> createLongExpression(element) ?: return
            else -> return
        }
        element.replaced(newExpression)
    }

    override fun isApplicableTo(element: KtConstantExpression): Boolean {
        val elementType = element.getType(element.analyze()) ?: return false
        return KotlinBuiltIns.isInt(elementType) || KotlinBuiltIns.isLong(elementType)
    }

    abstract fun createIntegerExpression(element: KtConstantExpression): KtExpression?
    abstract fun createLongExpression(element: KtConstantExpression): KtExpression?
}