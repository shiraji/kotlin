// "Remove redundant spread operator" "true"

fun foo(vararg x: Long) {}

fun bar() {
    foo(*longArrayOf<caret>(1L))
}

// TOOL: org.jetbrains.kotlin.idea.inspections.RemoveRedundantSpreadOperatorInspection