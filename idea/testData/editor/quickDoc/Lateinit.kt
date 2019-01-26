class Foo {
    <caret>lateinit var foo: String
}

//INFO: lateinit allows initializing a non-null property outside of a constructor