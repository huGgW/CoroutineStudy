import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture


fun main() {
    exampleWithTimeout()
}

// coroutineScope과 같고 추가로 timeout시 예외를 던짐 (OrNull인 경우는 null을 반환)
fun exampleWithTimeout() = runBlocking {
    // withTimeoutOrNull일 경우 null을 반환
    withTimeout(2000L) {
        // sleeping 4 출력 전 TimeoutCancellationException 발생
        repeat(1000) {
            printWithThread("I'm sleeping $it ...")
            delay(500L)
        }
    }
}


// coroutineScope, withContext에 의해 Start -> Result -> End 순서로 실행됨.
fun exampleCoroutineScopeMethod() = runBlocking {
    printWithThread("Start")
    printWithThread("Result - coroutineScope: ${calculateResult()}")
    printWithThread("Result - withContext: ${calculateResultWithContext()}")
    printWithThread("End")
}

// coroutineScope은 추가로 coroutine을 생성하고 바로 해당 block을 실행, 끝날 때까지 대기한다.
// 여러 개의 coroutine을 병렬적으로 생성하고 결과를 합치는 경우 사용.
suspend fun calculateResult() = coroutineScope {
    val result1 = async {
        delay(1000L)
        10
    }

    val result2 = async {
        delay(1000L)
        20
    }

    result1.await() + result2.await()
}

// withContext는 coroutineScope과 동일하나 context 변경이 추가됨.
suspend fun calculateResultWithContext() = withContext(Dispatchers.Default) {
    val result1 = async {
        delay(1000L)
        10
    }

    val result2 = async {
        delay(1000L)
        20
    }

    result1.await() + result2.await()
}


// 각 구현체 비동기 라이브러리별로 동일한 suspend interface 구현 가능
interface AsyncLibraryInterface {
    suspend fun longCall(): Unit
}
class AsyncLibraryImpl: AsyncLibraryInterface {
    override suspend fun longCall() {
        delay(1000L)
    }
}

// Suspend function을 통해 비동기 라이브러리에 관계없이 상위 함수는 동일하게, kotlin type으로 작성 가능.
fun exampleSuspendLibraryRefactoring(): Unit = runBlocking {
    val result1 = call1()
    val result2 = call2(result1)

    printWithThread(result2)
}

suspend fun call1(): Int {
    return CoroutineScope(Dispatchers.Default).async {
        Thread.sleep(1000L)
        100
    }.await()
}

suspend fun call2(num: Int): Int {
    // coroutine은 다양한 비동기 라이브러리에 대한 확장함수를 제공함.
    return CompletableFuture.supplyAsync {
        Thread.sleep(1000L)
        num * 2
    }.await() // CompletableFuture를 Coroutine으로 변환해주는 확장함수
}


// 문제점: Deferred Type에 의존하고 있음.
// 다른 비동기 라이브러리 코드를 사용하려하면 상위 레벨까지 코드 수정이 전파됨.
fun exampleDeferredBadDepend(): Unit = runBlocking {
    val result1: Deferred<Int> = async {
        deferredcall1()
    }

    val result2: Deferred<Int> = async {
        deferredcall2(result1.await())
    }

    printWithThread(result2.await())
}

fun deferredcall1(): Int {
    Thread.sleep(1000L)
    return 100
}

fun deferredcall2(num: Int): Int {
    Thread.sleep(1000L)
    return num * 2
}

class ExampleSuspendNotAlwaysPause {
    // Suspend function의 경우 반드시 pause되는 것은 아님.
    // 따라서 아래의 경우 a() -> c() -> b() 순서로 실행되지 않음.
    fun exampleSuspendNotAlwaysPause(): Unit = runBlocking {
        launch {
            a()
            b()
        }

        launch {
            c()
        }
    }

    suspend fun a() {
        printWithThread("A")
    }

    suspend fun b() {
        printWithThread("B")
    }

    suspend fun c() {
        printWithThread("C")
    }
}
