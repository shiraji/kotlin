// FILE: 1.kt

package a

import b.*
import kotlin.reflect.KClass

class A

val f: KClass<a.A> = A::class

// FILE: 2.kt

package b

object A
