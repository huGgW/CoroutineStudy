import kotlinx.coroutines.*

fun main() {
}

fun exampleCatchCancellationException(): Unit = runBlocking {
    // delay 등도 내부에서 CancellationException을 던져 취소를 받는다.
    val job = launch {
        try {
            delay(1000L)
        } catch (e: CancellationException) { // 따라서 이를 catch할 경우 coroutine의 취소는 발생되지 않는다!
            // Do nothing
        } finally {
            // 취소 시 exception으로 처리되어 try finally로 필요한 자원을 항상 닫을 수 있다.
        }

        printWithThread("delay에 의해 취소되지 않았다!")
    }

    delay(100L)
    printWithThread("취소 시작")
    job.cancel()
}

fun exampleCancellationOnDiffThread(): Unit = runBlocking {
    // Dispatcher Default로 인해 다른 thread에서 job이 실행됨
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번쨰 출력!!")
                nextPrintTime += 1000L
            }

            // Cancel 명령을 받았을 때 job을 취소시킴
            if (!this.isActive) {
                throw CancellationException()
            }
        }
    }

    // 따라서 job이 실행되고 나서 곧바로 delay가 실행되고
    // 결과적으로 출력은 1번만 발생하게 된다!
    delay(100L)
    job.cancel()
}

fun exampleCancellationOnSingleThread(): Unit = runBlocking {
    // 그러나 launch의 default behavior는 같은 thread에서 실행되는 것이므로
    // cancel은 job이 끝날때까지 실행되지 않음.
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번쨰 출력!!")
                nextPrintTime += 1000L

            }

            // Cancel 명령을 받았을 때 job을 취소시킴
            if (!this.isActive) {
                throw CancellationException()
            }
        }
    }

    // 따라서 5까지 모두 출력되게 된다.
    delay(100L)
    job.cancel()
}

fun exampleBusyLoop(): Unit = runBlocking {
    // job이 쉬지 않고 loop를 돌아 cancel이 중간에 실행되지 않음
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

    // 따라서 5까지 모두 출력되게 된다.
    delay(100L)
    job.cancel()
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
