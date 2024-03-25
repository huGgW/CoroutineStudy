import kotlinx.coroutines.*

fun main() {
}

fun exampleExceptionHandlingCoroutineExceptionHandler(): Unit = runBlocking {
    // 예외가 던져졌을 때, CoroutineExceptionHandler가 이를 받아 처리함!
    val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        printWithThread("예외처리")
    }

    // job이 root coroutine이어야 하고, launch에만 적용 가능하다.
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1000L)
}

fun exampleExceptionHandlingTryCatch(): Unit = runBlocking {
    // Try Catch로 내부에서 정상적으로 처리
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
    }
}

fun exampleSupervisorJob(): Unit  = runBlocking {
    // SupervisorJob은 자식 coroutine이 부모에게 예외를 전파하지 않도록 한다.
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }

    delay(1000L)
    job.await()
}

fun exampleChildAsyncException(): Unit = runBlocking {
    // 기본적으로 자식 coroutine의 예외는 부모 coroutine으로 전파된다.
    // 따라서 이 경우 async임에도 불구하고 부모 runBlocking으로 에러가 전파, 바로 에러를 출력하고 종료하게 된다.
    // 예외: CancellationException (이는 전파 없이 해당 coroutine을 취소해버림)
    val job = async {
        throw IllegalArgumentException()
    }

    delay(1000L)
    job.await()
}

fun exampleAsyncException(): Unit = runBlocking {
    // Async는 예외 발생시 coroutine이 종료되지만 이를 출력하지 않음
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    // 실제 예외를 확인 (발생) 위해서는 await을 통해 진행해야 한다.
    delay(1000L)
    job.await()
}

fun exampleLaunchException(): Unit = runBlocking {
    // Launch는 예외 발생시 이를 출력하고 coroutine이 종료됨
    val job = CoroutineScope(Dispatchers.Default).launch {
        throw IllegalArgumentException()
    }
}

fun exampleCoroutineScope(): Unit = runBlocking {
    // CoroutineScope을 새로 생성하면, 해당 launch를 root로 하는 새로운 tree 생성
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000L)
        printWithThread("Job 1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000L)
        printWithThread("Job 2")
    }
}