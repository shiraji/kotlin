// "Remove redundant spread operator" "true"

fun foo(vararg x: Int) {}

fun bar() {
    foo(*intArrayOf<caret>(1))
}

// TOOL: org.jetbrains.kotlin.idea.inspections.RemoveRedundantSpreadOperatorInspection