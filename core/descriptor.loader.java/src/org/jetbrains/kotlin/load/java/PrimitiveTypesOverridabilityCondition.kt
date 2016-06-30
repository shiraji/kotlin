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

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaMethodDescriptor
import org.jetbrains.kotlin.load.kotlin.JvmType
import org.jetbrains.kotlin.load.kotlin.mapValueParameterType
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition

class PrimitiveTypesOverridabilityCondition : ExternalOverridabilityCondition {
    override fun isOverridable(
            superDescriptor: CallableDescriptor,
            subDescriptor: CallableDescriptor,
            subClassDescriptor: ClassDescriptor?
    ): ExternalOverridabilityCondition.Result {
        if (subDescriptor !is JavaMethodDescriptor || superDescriptor !is FunctionDescriptor) return ExternalOverridabilityCondition.Result.UNKNOWN
        assert(subDescriptor.valueParameters.size == superDescriptor.valueParameters.size) {
            "External overridability condition with CONFLICTS_ONLY should not be run with different value parameters size"
        }

        for ((subParameter, superParameter) in subDescriptor.original.valueParameters.zip(superDescriptor.original.valueParameters)) {
            val isSubPrimitive = mapValueParameterType(subDescriptor, subParameter) is JvmType.Primitive
            val isSuperPrimitive = mapValueParameterType(superDescriptor, superParameter) is JvmType.Primitive

            if (isSubPrimitive != isSuperPrimitive) {
                return ExternalOverridabilityCondition.Result.INCOMPATIBLE
            }
        }

        return ExternalOverridabilityCondition.Result.UNKNOWN
    }

    override fun getContract() = ExternalOverridabilityCondition.Contract.CONFLICTS_ONLY
}
