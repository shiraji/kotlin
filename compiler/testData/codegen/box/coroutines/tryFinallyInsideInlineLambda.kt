
class Controller {
    suspend fun suspendHere(v: String, x: Continuation<String>) {
        x.resume(v)
    }
}

//fun builder(c: Controller.() -> Unit) {
//    //c(Controller()).resume(Unit)
//}

fun builder(coroutine c: Controller.() -> Continuation<Unit>) {
    c(Controller()).resume(Unit)
}


inline fun run(block: () -> Unit) {
    block()
}

fun box(): String {
    var result = ""
    run {
        builder {
            result += ""
        }
    }



    return result
}
