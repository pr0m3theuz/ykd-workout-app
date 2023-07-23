/** You can edit, run, and share this code. play.kotlinlang.org */
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

data class SetModel(
    val setID: Long =
        LocalDateTime.now()
            .toEpochSecond(ZoneOffset.UTC)
            .toString()
            .plus(Random.nextUInt().toString().substring(0..5))
            .toLong(),
    val lazyListItemID: Long =
        LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999)),
    var uuid: UUID = UUID.randomUUID(),
    var setNumber: Int? = 0,
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var targetWeight: Double? = null,
    var targetRepetitions: String? = null,
    var targetRatePerceivedEffort: String? = null,
    var targetRepetitionsInReserve: String? = null,
    var percentage1RM: Double? = 1.0,
    var setCompleted: Boolean = false,
    var setTypeID: Int = 1,
    var exerciseID: Long,
    var exerciseUUID: UUID,
    var workoutID: Long,
    var workoutUUID: UUID,
    var progressionSchemeID: Int = 1,
    var programUUID: UUID? = null,
    var mesocycleUUID: UUID? = null,
    var microcycleUUID: UUID? = null,
)

fun main() {

    val timestamp: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    val randomLong: String = Random.nextUInt().toString()

    println("${timestamp.plus(randomLong.substring(0..5)).toLong()}")

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

    println("${LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))}")

    println("${UUID.randomUUID()}")

    println(LocalDateTime.now().toString())

    val workoutID = listOf(
        1689652211395230,
        1689652211254261,
        1689652211196462,
        1689652211321974,
        1689652211250054,
        1689652211400304,
        1689652211169313,
        1689652211150278,
        1689652211496928,
        1689652340249517,
        1689652340872696
    )

    val workoutUUID = listOf(
        UUID.fromString("6f25bdf2-6a5f-436c-9974-3c7ffdfe0b7b"),
        UUID.fromString("75081753-8fbb-4dd0-b894-f7ececd32d38"),
        UUID.fromString("8918e09a-cd44-41e7-ad8a-841e9bccfaa8"),
        UUID.fromString("321ca33e-8559-44a6-869a-d559fb924e36"),
        UUID.fromString("9034135f-b59d-4a28-90e6-803dde8e762e"),
        UUID.fromString("5c26e49b-4564-4fd9-82ea-31e3c1b9d76c"),
        UUID.fromString("722e936a-76e7-4083-8198-019eeb054ea9"),
        UUID.fromString("f89350fb-4482-4dbb-ae62-d0d58f221f06"),
        UUID.fromString("8c5e44ad-c386-4f65-a79c-e528c3ce5def"),
        UUID.fromString("3b9070a9-0970-40f5-9347-d9dce6fd818a"),
        UUID.fromString("f705ee12-755a-42d7-aeae-ea4c508e9d1a")
    )


    val exerciseID = listOf(
        1687474247371298,
        1687474247371100,
        1687474247370780,
        1687474247370992,
        1687474247370918,
        1687474247371632,
        1687474247371900,
        1687474247371985,
        1687474247371448,
    )

    val uuid = listOf(
        UUID.fromString("38a82f11-8598-4888-b812-d5717bfbd306"),
        UUID.fromString("e9db1e30-b3f2-490c-b6bc-6644d3882893"),
        UUID.fromString("3980bf68-3865-492f-9093-c71239ceadca"),
        UUID.fromString("d17bd373-7f3b-4ae9-b2d5-3936a193a5eb"),
        UUID.fromString("9c7f1525-38b2-4771-a677-39139d9eefdc"),
        UUID.fromString("b01e5e93-5674-44b6-bc1c-c401d0cd5a21"),
        UUID.fromString("ae53b2b0-00d7-40bc-aa1b-8aa4678348cc"),
        UUID.fromString("d939d5e3-b8e7-465f-8e0e-0eabde5c1df6"),
        UUID.fromString("44959ef9-a04e-48ad-893b-70f72a1364a6")
    )

    var count: Int = 0

    for (workout in 0..10) {
        count++
        for (exercise in 0..8) {
            for (set in 1..3) {
                println(
                    "${
                        SetModel(
                            setNumber = set,
                            targetRepetitions = "15-20",
                            targetRepetitionsInReserve = "4",
                            exerciseID = exerciseID[exercise],
                            exerciseUUID = uuid[exercise],
                            workoutID = workoutID[workout],
                            workoutUUID = workoutUUID[workout],
                            programUUID = UUID.fromString("d5d86646-7476-4225-a7df-e6d0cb4706c9"),
                            mesocycleUUID = UUID.fromString("ed10dbe4-541e-4a1a-938f-4403f0cf1528"),
                            microcycleUUID = UUID.fromString("c13a3cc7-fc77-4d1c-8565-c37dbdd3c30d"),
                        )
                    }"
                )
            }
        }

    }
}
