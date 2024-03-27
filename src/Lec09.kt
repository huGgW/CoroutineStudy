import kotlinx.coroutines.delay

suspend fun main() {
    val userService = UserService()
    val userDto = userService.findUser(1)
    println(userDto)
}

interface Continuation {
    suspend fun resumeWith(data: Any?)
}

class UserService {
    private val userProfileRepository = UserProfileRepository()
    private val userImageRepository = UserImageRepository()

    private abstract class FindUserContinuation: Continuation {
        var label = 0
        var profile: UserProfile? = null
        var image: UserImage? = null
    }

    suspend fun findUser(userId: Long, continuation: Continuation? = null): UserDto {
        val sm = continuation as? FindUserContinuation
            ?: object : FindUserContinuation() {
                override suspend fun resumeWith(data: Any?) {
                    when (label) {
                        0 -> {
                            profile = data as UserProfile
                            label = 1
                        }

                        1 -> {
                            image = data as UserImage
                            label = 2
                        }
                    }
                    findUser(userId, this)
                }
            }

        when (sm.label) {
            0 -> { // 0단계: 초기 시작
                println("프로필을 가져오겠습니다.")
                userProfileRepository.findProfile(userId, sm)
            }
            1 -> { // 1단계: 1차 중단 후 재시작
                println("이미지를 가져오겠습니다.")
                userImageRepository.findImage(sm.profile!!, sm)
            }
        }

        return UserDto(sm.profile!!, sm.image!!)
    }
}

class UserServiceOriginal {
    private val userProfileRepository = UserProfileRepository()
    private val userImageRepository = UserImageRepository()

    // 주석: suspend를 통해 일시중지되고 context switch가 일어날 수 있는 부분들
    suspend fun findUser(userId: Long): UserDto {
         // 0단계: 초기 시작
        println("프로필을 가져오겠습니다.")
        val profile = userProfileRepository.findProfile(userId)

         // 1단계: 1차 중단 후 재시작
        println("이미지를 가져오겠습니다.")
        val image = userImageRepository.findImage(profile)

         // 2단계: 2차 중단 후 재시작
        return UserDto(profile, image)
    }
}

class UserProfileRepository {
    suspend fun findProfile(userId: Long, continuation: Continuation) {
        delay(1000L)
        continuation.resumeWith(UserProfile("name"))
    }

    suspend fun findProfile(userId: Long): UserProfile {
        delay(1000L)
        return UserProfile("name")
    }
}

class UserImageRepository {
    suspend fun findImage(profile: UserProfile, continuation: Continuation) {
        delay(1000L)
        continuation.resumeWith(UserImage("imageUrl"))
    }

    suspend fun findImage(profile: UserProfile): UserImage {
        delay(1000L)
        return UserImage("imageUrl")
    }
}



data class UserDto(
    val profile: UserProfile,
    val image: UserImage
)

data class UserProfile (
    val name: String,
)

data class UserImage (
    val url: String,
)