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
import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.psi.KtIfExpression

class RemoveRedundantIfInspection : IntentionBasedInspection<KtIfExpression>(RemoveRedundantIfIntention::class)

class RemoveRedundantIfIntention : SelfTargetingOffsetIndependentIntention<KtIfExpression>(
        KtIfExpression::class.java,
        "Remove redundant 'if'"
) {
    override fun applyTo(element: KtIfExpression, editor: Editor?) {
    }

    override fun isApplicableTo(element: KtIfExpression): Boolean {
        val elseExpression = element.`else` ?: return false
        return false
    }
}