<caret>tailrec fun foo() {
    foo()
}

//INFO: tailrec marks a function as tail-recursive  (allowing the compiler to replace recursion with iteration)