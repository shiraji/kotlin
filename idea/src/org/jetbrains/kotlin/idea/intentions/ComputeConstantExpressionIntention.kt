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
import org.jetbrains.kotlin.idea.KotlinLightConstantExpressionEvaluator
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.analyzeFully
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class ComputeConstantExpressionIntention : SelfTargetingOffsetIndependentIntention<KtBinaryExpression>(KtBinaryExpression::class.java, "") {

    override fun applyTo(element: KtBinaryExpression, editor: Editor?) {
        val targetExpression = element.getStrictParentOfType<KtBinaryExpression>() ?: element


        val resolvedCall = targetExpression.getResolvedCall(targetExpression.analyzeFully())


        try {
            val foo = KotlinLightConstantExpressionEvaluator().computeConstantExpression(targetExpression, true)
            println(foo)
        }
        catch (e: Exception) {
            return
        }
    }

    override fun isApplicableTo(element: KtBinaryExpression): Boolean {
        val targetExpression = element.getStrictParentOfType<KtBinaryExpression>() ?: element
        text = "Compute constant value of '${targetExpression.text}'"

        val type = targetExpression.getType(targetExpression.analyze()) ?: return false
        if (!KotlinBuiltIns.isInt(type) && !KotlinBuiltIns.isLong(type) && !KotlinBuiltIns.isDouble(type) && !KotlinBuiltIns.isFloat(type)) return false
        return targetExpression.isConstantExpression()
    }

    fun KtBinaryExpression.isConstantExpression(): Boolean {
        if (right !is KtConstantExpression) return false
        val leftExpression = left
        return when (leftExpression) {
            is KtConstantExpression -> true
            is KtBinaryExpression -> leftExpression.isConstantExpression()
            else -> false
        }
    }
}