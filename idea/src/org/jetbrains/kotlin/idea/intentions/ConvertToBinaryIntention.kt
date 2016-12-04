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

import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.createExpressionByPattern

class ConvertToBinaryIntention : ConvertNumeralSystemIntention("Convert to binary") {
    override fun createIntegerExpression(element: KtConstantExpression): KtExpression? {
        return KtPsiFactory(element).createExpressionByPattern("0b$0", java.lang.Integer.toBinaryString(element.text.parseInt() ?: return null))
    }

    override fun createLongExpression(element: KtConstantExpression): KtExpression? {
        return KtPsiFactory(element).createExpressionByPattern("0b$0L", java.lang.Long.toBinaryString(element.text.parseLong() ?: return null))
    }

    override fun isApplicableTo(element: KtConstantExpression): Boolean {
        if (element.text.hasBinaryPrefix()) return false
        return super.isApplicableTo(element)
    }
}