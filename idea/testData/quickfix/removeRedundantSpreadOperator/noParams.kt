// "Remove redundant spread operator" "true"

fun foo(vararg x: String) {}

fun bar() {
    foo(*arrayOf<caret>())
}

// TOOL: org.jetbrains.kotlin.idea.inspections.RemoveRedundantSpreadOperatorInspection