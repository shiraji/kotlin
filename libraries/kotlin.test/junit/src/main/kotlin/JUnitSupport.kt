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

package kotlin.test.junit

import org.junit.*
import kotlin.test.*
import kotlin.AssertionError
import kotlin.IllegalArgumentException
import kotlin.UnsupportedOperationException
import kotlin.IndexOutOfBoundsException
import kotlin.IllegalStateException

class JUnitContributor : AsserterContributor {
    override fun contribute(): Asserter? {
        for (stackFrame in currentStackTrace()) {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            val className = stackFrame.className as java.lang.String

            if (className.startsWith("org.junit.") || className.startsWith("junit.")) {
                return JUnitAsserter
            }
        }

        return null
    }
}

object JUnitAsserter : Asserter {
    override fun assertEquals(message : String?, expected : Any?, actual : Any?) {
        Assert.assertEquals(message, expected, actual)
    }

    override fun assertNotEquals(message : String?, illegal : Any?, actual : Any?) {
        Assert.assertNotEquals(message, illegal, actual)
    }

    override fun assertNotNull(message : String?, actual : Any?) {
        Assert.assertNotNull(message ?: "actual value is null", actual)
    }

    override fun assertNull(message : String?, actual : Any?) {
        Assert.assertNull(message ?: "actual value is not null", actual)
    }

    override fun fail(message : String?): Nothing {
        Assert.fail(message)
        // should not get here
        throw AssertionError(message)
    }
}
