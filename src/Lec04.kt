import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번쨰 출력!!")
                nextPrintTime += 1000L

            }
        }
    }
}

// delay, yield 같은 coroutine package의 suspend 함수들은 cancel에 협조한다!
fun delayCancelExample(): Unit = runBlocking {
    val job1 = launch {
        delay(1000L)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000L)
        printWithThread("Job 2")
    }

    delay(100)
    job1.cancel()
}
