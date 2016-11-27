// "Remove redundant 'if-return' statement" "true"
fun bar(value: Int): Boolean {
    <caret>if (value % 2 == 0) {
        return false
    }
    else {
        return true
    }
}