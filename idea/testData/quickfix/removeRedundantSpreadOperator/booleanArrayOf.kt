// "Remove redundant spread operator" "true"

fun foo(vararg x: Boolean) {}

fun bar() {
    foo(*booleanArrayOf<caret>(true, true))
}

// TOOL: org.jetbrains.kotlin.idea.inspections.RemoveRedundantSpreadOperatorInspection