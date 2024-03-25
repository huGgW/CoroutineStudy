import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit {
}

/*
2. runBlocking은 Completing 상태로 대기중.
   이 때 child로부터 전파된 예외를 받아 Cancelling 상태로 변환.
   이 후 children들에게 취소 요청을 전달.
 */
/*
4. 모든 children이 취소되면 runBlocking은 Cancelled 상태로 변환.
   이후 예외를 출력하고 runBlocking을 종료.
 */
fun exampleStructured2(): Unit = runBlocking {
    /*
    3. 해당 coroutine은 delay 중 부모로부터 cancel 요청을 받게 됨.
       상태가 Cancelling으로 변경됨.
       이후 coroutine은 종료되고 상태는 Cancelled로 변경됨.
     */
    launch {
        delay(600)
        printWithThread("A")
    }

    /*
    1. 해당 coroutine에서 예외를 발생,
       상태가 Canceled로 변경됨.
       부모에게로 예외가 전파.
     */
    launch {
        delay(500L)
        throw IllegalArgumentException("코루틴 실패")
    }
}


/*
3. runBlocking은 Completing 상태로 대기중.
   이 때 child로부터 전파된 예외를 받아 Cancelling 상태로 변환.
   이 후 상태는 Cancelled로 변환, 예외를 출력하고 runBlocking을 종료.
 */
fun exampleStructured1(): Unit = runBlocking {
    /* 1. 해당 coroutine이 정상적으로 실행. Completed 상태가 됨. */
    launch {
        delay(500L)
        printWithThread("A")
    }

    /*
    2. 해당 coroutine이 예외를 발생,
       상태가 Canceled로 변경됨.
       부모에게로 예외가 전파.
     */
    launch {
        delay(600L)
        throw IllegalArgumentException("코루틴 실패")
    }
}
