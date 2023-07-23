/** You can edit, run, and share this code. play.kotlinlang.org */
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

LocalDateTime.now()
.toEpochSecond(ZoneOffset.UTC)
.toString()
.plus(Random.nextUInt().toString().substring(0..5))
.toLong(),

LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999)),

data class Mesocycle(
    val mesocycleID: Long = LocalDateTime.now()
        .toEpochSecond(ZoneOffset.UTC)
        .toString()
        .plus(Random.nextUInt().toString().substring(0..5))
        .toLong(),
    val lazyListItemID: Long = LocalDateTime.now().format(formatter).toLong()
        .div(Random.nextLong(1, 999)),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var description: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var order: Int,
    val programID: Long? = null,
)

fun main() {

    val timestamp: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    val randomLong: String = Random.nextUInt().toString()

    println("${timestamp.plus(randomLong.substring(0..5)).toLong()}")

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

    println("${LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))}")

    println("${UUID.randomUUID()}")

    println(LocalDateTime.now().toString())

//   val formattedText =
//     listOf()

    var count: Int = 0

    for (block in 1..2) {
        count++
        for (micro in 4..4) {

            for (workout in 1..2) {

                println(
                    "${
                        Mesocycle(
                            name = "",
                            order = workout,
                            programID = 1689553055424273,
                        )
                    }"
                )
            }
        }
    }
}
