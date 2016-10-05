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
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import java.util.*

class RemoveRedundantCallsOfConversionMethodsInspection : IntentionBasedInspection<KtDotQualifiedExpression>(RemoveRedundantCallsOfConversionMethodsIntention::class)

class RemoveRedundantCallsOfConversionMethodsIntention : SelfTargetingOffsetIndependentIntention<KtDotQualifiedExpression>(KtDotQualifiedExpression::class.java, "Remove redundant calls of the conversion method") {

    private val targetClassMap = mapOf("toList()" to List::class.qualifiedName,
                                       "toSet()" to Set::class.qualifiedName,
                                       "toMap()" to Map::class.qualifiedName,
                                       "toMutableList()" to "kotlin.collections.MutableList",
                                       "toMutableSet()" to "kotlin.collections.MutableSet",
                                       "toMutableMap()" to "kotlin.collections.MutableMap",
                                       "toSortedSet()" to SortedSet::class.qualifiedName,
                                       "toSortedMap()" to SortedMap::class.qualifiedName,
                                       "toString()" to String::class.qualifiedName,
                                       "toDouble()" to Double::class.qualifiedName,
                                       "toFloat()" to Float::class.qualifiedName,
                                       "toLong()" to Long::class.qualifiedName,
                                       "toInt()" to Int::class.qualifiedName,
                                       "toChar()" to Char::class.qualifiedName,
                                       "toShort()" to Short::class.qualifiedName,
                                       "toByte()" to Byte::class.qualifiedName)


    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
        element.replaced(element.receiverExpression)

        mutableMapOf(1 to 1).toMutableMap()
    }

    override fun isApplicableTo(element: KtDotQualifiedExpression): Boolean {
        val selectorExpressionText = element.selectorExpression?.text
        val qualifiedName = targetClassMap[selectorExpressionText] ?: return false
        val receiverExpression = element.receiverExpression
        return when (receiverExpression) {
            is KtStringTemplateExpression -> String::class.qualifiedName
            is KtConstantExpression -> receiverExpression.getType(receiverExpression.analyze())?.getJetTypeFqName(false)
            else -> receiverExpression.getResolvedCall(receiverExpression.analyze())?.candidateDescriptor?.returnType?.getJetTypeFqName(false)
        } == qualifiedName
    }
}