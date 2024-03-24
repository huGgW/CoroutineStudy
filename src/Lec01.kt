import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/*
실행 순서:
1. runBlocking: coroutine을 생성
2. println("Start"): 출력
3. launch: coroutine을 생성
4. yield: coroutine을 중단하고 다른 coroutine에게 실행을 양보
5. newRoutine을 실행
6. yield: coroutine을 중단하고 다른 coroutine에게 실행을 양보
7. println("End"): 출력
8. println(num1 + num2): 출력
9. 종료
 */

// runBlocking: block 안을 coroutine으로 생성
fun main(): Unit = runBlocking {
    printWithThread("Start")

    // launch: 반환값이 없는 coroutine을 생성
    launch {
        newRoutine()
    }

    // yield: 지금 coroutine을 중단하고 다른 coroutine에게 실행을 양보 (thread를 양보)
    yield()

    printWithThread("End")
}

// suspend fun: 다른 suspend function을 호출 가능
suspend fun newRoutine() {
    val num1 = 1
    val num2 = 2
    yield()

    printWithThread(num1 + num2)
}

fun printWithThread(str: Any) {
    println("[${Thread.currentThread().name}] $str")
}