// WITH_RUNTIME
// SKIP_ERRORS_AFTER

fun foo() {
    val foo: String? = null
    foo?.let {
        it.to("").to("")<caret>
    }
}