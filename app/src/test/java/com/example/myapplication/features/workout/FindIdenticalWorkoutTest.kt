@file:OptIn(ExperimentalSerializationApi::class)

import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.util.generateWorkoutSignature
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi

class FindIdenticalWorkoutTest {

  // Configure kotlinx.serialization Json instance
// It needs to know about custom serializers if they are not contextually available
// Add modules if your serializers require them (e.g., for ObjectId, Uuid if not standard)
  private val json = Json {
    ignoreUnknownKeys = true // Recommended for flexibility
    isLenient = true         // If JSON might be slightly malformed
    prettyPrint = false      // Not needed for deserialization test
    // Explicitly register serializers if needed (often not necessary with @Serializable annotation)
    // serializersModule = SerializersModule {
    //     contextual(ObjectId::class, ObjectIdSerializer)
    //     contextual(Uuid::class, UuidSerializer)
    // }
  }

  val filepath =
    "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/test/java/com/example/myapplication/resources/features/Workout_cb_exported.json"

  @OptIn(ExperimentalStdlibApi::class)
  val workouts =
    File(filepath).bufferedReader().readLines().map {
      val workout = json.decodeFromString<Workout>(it)
      workout.signature = generateWorkoutSignature(workout, includeTargets = true)
      workout.signatureHash =
        MessageDigest.getInstance("SHA-256").digest(workout.signature!!.toByteArray(Charsets.UTF_8))
          .toHexString()
      return@map workout
    } // [cite: 1] (structure adapted)

  @OptIn(ExperimentalUuidApi::class)
  @Example
  fun testWorkoutDeserializationFromJson() {
    // Assertions to verify the deserialized object's properties
    assertNotNull(workouts, "Workout object should not be null")
    println(
      "Number of workouts: ${workouts.size}"
    )

    val string = StringBuilder("Number of Max Muscle workouts: ")
    string.append("${workouts.filter { workout -> workout.ownerID == "MAX_MUSCLE" }.size}\n")
    string.append("Max Muscle workout signature: ")
    string.append("${workouts.first { workout -> workout.ownerID == "MAX_MUSCLE" }.signature}\n")
    string.append("Max Muscle workout signature hash: ")
    string.append("${workouts.first { workout -> workout.ownerID == "MAX_MUSCLE" }.signatureHash}\n")
    string.append("Max Muscle workout signature hash: ")
    val maxMuscleWorkoutsGroupedBySignatureHash =
      workouts.filter { workout -> workout.ownerID == "MAX_MUSCLE" }
        .groupBy { workout -> workout.signatureHash }
    string.append("keys size: ${maxMuscleWorkoutsGroupedBySignatureHash.keys.size}\n")
    maxMuscleWorkoutsGroupedBySignatureHash.forEach { string.append("key ${it.key} value size: ${it.value.size}\n") }

    string.appendLine()
    string.append("Number of Raider workouts: ${workouts.filter { workout -> workout.ownerID == "RAIDER" }.size}\n")
    string.append("Raider workout signature: ")
    string.append("${workouts.first { workout -> workout.ownerID == "RAIDER" }.signature}\n")
    string.append("Raider workout signature hash: ")
    string.append("${workouts.first { workout -> workout.ownerID == "RAIDER" }.signatureHash}\n")
    string.append("Raider workout signature hash: ")
    val raiderWorkoutsGroupedBySignatureHash =
      workouts.filter { workout -> workout.ownerID == "RAIDER" }
        .groupBy { workout -> workout.signatureHash }
    string.append("keys size: ${raiderWorkoutsGroupedBySignatureHash.keys.size}\n")
    raiderWorkoutsGroupedBySignatureHash.forEach { string.append("key ${it.key} value size: ${it.value.size}\n") }

    println(string)
  }

  @Example
  fun testIdentifyIdenticalWorkouts() {

  }
}