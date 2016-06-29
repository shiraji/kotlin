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

@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("RangesKt")
package kotlin.ranges
import kotlin.IllegalArgumentException
import kotlin.UnsupportedOperationException
import kotlin.IndexOutOfBoundsException
import kotlin.IllegalStateException
/**
 * Represents a range of [Comparable] values.
 */
private class ComparableRange<T: Comparable<T>> (
        override val start: T,
        override val endInclusive: T
): ClosedRange<T> {

    override fun equals(other: Any?): Boolean {
        return other is ComparableRange<*> && (isEmpty() && other.isEmpty() ||
                start == other.start && endInclusive == other.endInclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * start.hashCode() + endInclusive.hashCode()
    }

    override fun toString(): String = "$start..$endInclusive"
}

/**
 * Creates a range from this [Comparable] value to the specified [that] value. This value
 * needs to be smaller than [that] value, otherwise the returned range will be empty.
 */
public operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T> = ComparableRange(this, that)


internal fun checkStepIsPositive(isPositive: Boolean, step: Number) {
    if (!isPositive) throw IllegalArgumentException("Step must be positive, was: $step.")
}
