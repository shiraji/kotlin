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

import org.jetbrains.kotlin.psi.*

class ConvertToWithIntention : ConvertToScopeIntention("Convert to with") {

    override fun createScopeExpression(factory: KtPsiFactory, element: KtExpression): KtExpression? {
        if (element !is KtDotQualifiedExpression) return null
        return factory.createExpressionByPattern("with($0) {}", element.getLeftMostReceiverExpression())
    }

    override fun findCallExpressionFrom(scopeExpression: KtExpression) = scopeExpression as? KtCallExpression
}