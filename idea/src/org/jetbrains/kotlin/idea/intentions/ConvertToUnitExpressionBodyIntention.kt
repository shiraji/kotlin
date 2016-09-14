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
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit

class ConvertToUnitExpressionBodyIntention : SelfTargetingOffsetIndependentIntention<KtNamedFunction>(
        KtNamedFunction::class.java, "Convert to expression body") {
    override fun applyTo(element: KtNamedFunction, editor: Editor?) {
        val body = element.bodyExpression ?: return
        element.addBefore(KtPsiFactory(element).createEQ(), body)
        body.replaced(KtPsiFactory(element).createExpression("Unit"))
    }

    override fun isApplicableTo(element: KtNamedFunction): Boolean {
        return element.hasBlockBody() && element.returnType()?.isUnit() ?: false
    }

    private fun KtNamedFunction.returnType(): KotlinType? {
        val descriptor = analyze(BodyResolveMode.PARTIAL)[BindingContext.DECLARATION_TO_DESCRIPTOR, this] ?: return null
        return (descriptor as FunctionDescriptor).returnType
    }

}