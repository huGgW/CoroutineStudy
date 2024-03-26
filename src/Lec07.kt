import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun main() {
}

fun exampleCoroutineDispatchers() = runBlocking{
    val jobDefault = launch(Dispatchers.Default) {
        printWithThread("Default")
    }

    val jobIO = launch(Dispatchers.IO) {
        printWithThread("IO")
    }

    // 외부 dependency가 필요. (주로 UI 관련 Thread)
//    val jobMain = launch(Dispatchers.Main) {
//        printWithThread("Main")
//    }

    // Java의 ExecutorPool을 이용하여 Coroutine Dispatcher 생성 가능
    val executorPoolDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    val jobExecutorPools = repeat(8) {
        launch(executorPoolDispatcher) {
            printWithThread("Executor Pool $it")
        }
    }
}


fun exampleCoroutineContext() {
    // CoroutineContext는 key-value(set) 형태로 구성되어 있음.
    // + operator가 override되어 여러 CoroutineContext를 합칠 수 있음.
    val job = CoroutineScope(
        CoroutineName("나만의 코루틴") + SupervisorJob() + Dispatchers.Default
    ).launch {
        delay(1000L)
        printWithThread("Job 1")

        // minusKey를 통해 해당 key의 CoroutineContext를 제거할 수 있음.
        coroutineContext.minusKey(CoroutineName.Key)
    }
}

// Class 내의 CoroutineScope을 지정하여
class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething() {
        scope.launch {
            // 무언가 코루틴이 시작되어 작업!
        }
    }

    fun destroy() {
        // 해당 class가 소멸 시 CoroutineScope의 cancel을 통해 모든 코루틴을 종료!
        scope.cancel()
    }
}

// launch, runBlocking 등은 CoroutineScope의 확장함수!
//suspend fun main(): Unit {
//    val job = CoroutineScope(Dispatchers.Default).launch {
//        delay(1000L)
//        printWithThread("Job 1")
//    }
//
//    job.join()
//}

//fun main(): Unit {
//    CoroutineScope(Dispatchers.Default).launch {
//        delay(1000L)
//        printWithThread("Job 1")
//    }
//
//    Thread.sleep(1500L)
//}
