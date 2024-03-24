import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main(): Unit {
}

class ExampleAsyncWithoutCallbackHell {
    fun exampleAsyncWithoutCallbackHell(): Unit = runBlocking {
        val time = measureTimeMillis {
            val xAddAsync = async { apiAdd1(1) }
            val xMultAsync = async { apiMult2(xAddAsync.await()) }
            val result = xMultAsync.await()

            printWithThread(result)
        }

        printWithThread("소요 시간: $time ms")
    }

    // 기존 방식: Callback Hell. 대략 아래와 같은 방식.
    // fun exampleCallbackHell(): Unit = runBlocking {
    //     val time = measureTimeMillis {
    //         apiAdd1(1) { x ->
    //             apiMult2(x) { y ->
    //                 printWithThread(x + y)
    //             }
    //         }
    //     }
    //
    //     printWithThread("소요 시간: $time ms")
    // }

    suspend fun apiAdd1(x: Int): Int {
        delay(1000L)
        return x + 1
    }

    suspend fun apiMult2(x: Int): Int {
        delay(1000L)
        return x * 2
    }
}

class ExampleMultipleAsync {
    fun exampleMultipleAsyncLazyStart(): Unit = runBlocking {
        val time = measureTimeMillis {
            val job1 = async(start=CoroutineStart.LAZY) { apiCall1() }
            val job2 = async(start=CoroutineStart.LAZY) { apiCall2() }

            // Lazy start인 경우, await이 호출되고 나서야 coroutine이 실행됨.
            printWithThread(job1.await() + job2.await())
        }

        // 따라서 실행 시간은 총 2초 조금 넘게 나옴.
        printWithThread("소요 시간: $time ms")
    }

    // 여러 API 호출을 동시에 실행하여 소요시간 감소
    fun exampleMultipleAsync(): Unit = runBlocking {
        val time = measureTimeMillis {
            val job1 = async { apiCall1() }
            val job2 = async { apiCall2() }

            // async로 인해 job1과 job2는 거의 동시에 실행됨.
            printWithThread(job1.await() + job2.await())
        }

        // job1과 job2가 거의 동시에 실행되므로 1초 조금 넘게 걸림.
        printWithThread("소요 시간: $time ms")
    }

    suspend fun apiCall1(): Int {
        delay(1000L)
        return 1
    }

    suspend fun apiCall2(): Int {
        delay(1000L)
        return 2
    }
}

fun exampleAsync(): Unit = runBlocking {
    // async는 반환값을 가짐.
    // Job을 상속하는 Deferred를 반환.
    val job = async {
        delay(1000L)
        5 + 3
    }

    // await: 대기 후 async의 반환값을 받음.
    val eight = job.await()
    printWithThread(eight)
}

fun exampleJobJoin(): Unit = runBlocking {
    // job1과 job2는 거의 동시에 실행됨.
    // (job1 실행 후 1초 대기동안 job2 실행)

    val job1 = launch {
        delay(1000L)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000L)
        printWithThread("Job 2")
    }

    // join: coroutine이 끝날 때까지 기다림.
    // 따라서 job3는 약 1.1초 후에 실행됨.
    job1.join()
    job2.join()
    val job3 = launch {
        delay(1000L)
        printWithThread("Job 3")
    }
}

fun exampleJobCancel(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    // job.cancel()을 호출하면 coroutine이 중단됨.
    // 따라서 1초 후에 coroutine이 중단됨. (2까지 출력됨)
    delay(1000L)
    job.cancel()
}

fun exampleJobStart(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }

    // start()를 호출해야 coroutine이 실행됨.
    // 따라서 1초 후에 "Hello launch"가 출력됨.
    delay(1000L)
    job.start()
}

fun exampleRunBlocking() {
    // runBlocking은 내부 coroutine이 끝날 때까지 해당 thread를 block함.
    // 따라서 자주 사용하면 안됨.
    runBlocking {
        printWithThread("Start")
        launch {
            // delay: coroutine을 주어진 시간만큼 중단, 다른 coroutine에게 실행을 양보
            delay(2000L)
            printWithThread("LAUNCH END")
        }
    }

    // runBlocking으로 인해 2초 후가 되서야 실행됨.
    printWithThread("END")
}