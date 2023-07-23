/** You can edit, run, and share this code. play.kotlinlang.org */
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

data class Workout(
    val workoutID: Long =
        LocalDateTime.now()
            .toEpochSecond(ZoneOffset.UTC)
            .toString()
            .plus(Random.nextUInt().toString().substring(0..5))
            .toLong(),
    val lazyListItemID: Long =
        LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999)),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var workoutAuthor: String? = null,
    var workoutDuration: Long = 0,
    var workoutType: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    val workoutStarted: LocalDateTime = LocalDateTime.now(),
    var workoutEnded: LocalDateTime? = null,
    var day: Int? = null,
    val programID: Long? = null,
    val mesocycleID: Long? = null,
    val microcycleID: Long? = null,
)

fun main() {

    val timestamp: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    val randomLong: String = Random.nextUInt().toString()

    println("${timestamp.plus(randomLong.substring(0..5)).toLong()}")

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

    println("${LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))}")

    println("${UUID.randomUUID()}")

    println(LocalDateTime.now().toString())

    val formattedText =
        listOf(
            1689565032360184,
            1689565032210071,
        )

    var count: Int = 0

    for (block in 1..2) {
        count++
        for (micro in 4..4) {

            for (workout in 1..2) {

                println(
                    "${
                        Workout(
                            name = "Day $workout - Break-In B$block:M$micro",
                            day = workout,
                            programID = 1689553055424273,
                            mesocycleID = 1689554270183622,
                            microcycleID = formattedText[count - 1],
                        )
                    }"
                )
            }
        }
    }
}
