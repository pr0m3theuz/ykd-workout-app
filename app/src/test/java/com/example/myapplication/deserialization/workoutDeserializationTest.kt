package com.example.myapplication.deserialization

import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutDao
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
import kotlin.uuid.ExperimentalUuidApi

class WorkoutDeserializationTest {

  // Configure kotlinx.serialization Json instance
  // It needs to know about custom serializers if they are not contextually available
  // Add modules if your serializers require them (e.g., for ObjectId, Uuid if not standard)
  private val json = Json {
    ignoreUnknownKeys = true // Recommended for flexibility
    isLenient = false         // If JSON might be slightly malformed
    prettyPrint = false      // Not needed for deserialization test
    // Explicitly register serializers if needed (often not necessary with @Serializable annotation)
    // serializersModule = SerializersModule {
    //     contextual(ObjectId::class, ObjectIdSerializer)
    //     contextual(Uuid::class, UuidSerializer)
    // }
  }

  @OptIn(ExperimentalUuidApi::class)
  @Example
  fun testWorkoutDeserializationFromJson() {
    // JSON string from the uploaded file example.txt [cite: 1]
    // NOTE: Removed "_default" and "Workout" top-level keys as they seem meta/incorrect JSON structure
    // for deserializing directly into the Workout object. Ensure your actual JSON source
    // *only* contains the Workout object's representation.
    val workoutJson =
      File("/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/test/java/com/example/myapplication/deserialization/workout_example.txt").readText() // [cite: 1] (structure adapted)

    // Deserialize the JSON string to a Workout object
    val workout = json.decodeFromString<Workout>(workoutJson)

    // Assertions to verify the deserialized object's properties
    assertNotNull(workout, "Workout object should not be null")

    // --- Verify fields based on the JSON [cite: 1] ---
    Assertions.assertEquals(
      workout.id.toString(),
      "ID should match JSON",
      "6779fb71b779f36f007c72c4"
    )
    Assertions.assertEquals(
      "dfa0c237-6890-4bdc-a507-c3e93c5de610",
      workout.uuid.toString(),
      "UUID should match JSON"
    )
    Assertions.assertEquals("Day 1 - Break-In B1:M1", workout.name, "Name should match JSON")
    Assertions.assertEquals(
      Instant.parse("2025-01-01T00:00:00.000000000Z"),
      workout.dateCreated,
      "DateCreated should match JSON"
    )
    Assertions.assertEquals(1, workout.day, "Day should match JSON")
    Assertions.assertEquals("MAX_MUSCLE", workout.ownerID, "OwnerID should match JSON")
    Assertions.assertEquals(true, workout.isPublic, "isPublic should match JSON")
    Assertions.assertEquals(
      "6779fb71b779f36f007c72be",
      workout.programID.toString(),
      "ProgramID should match JSON"
    )
    Assertions.assertEquals(
      "6779fb71b779f36f007c72c0",
      workout.mesocycleID.toString(),
      "MesocycleID should match JSON"
    )
    Assertions.assertEquals(
      "6779fb71b779f36f007c72c2",
      workout.microcycleID.toString(),
      "MicrocycleID should match JSON"
    )
    Assertions.assertEquals(
      7443574478591460,
      workout.lazyListItemID,
      "LazyListItemID should match JSON"
    )


    // --- Verify fields *not* in the specific JSON snippet [cite: 1] (should be default or null) ---
    Assertions.assertNull(workout.workoutAuthor, "WorkoutAuthor should be null")
    Assertions.assertTrue(workout.notes.isEmpty(), "Notes should be empty")
    Assertions.assertEquals(
      0,
      workout.workoutDuration,
      "WorkoutDuration should be null"
    ) // Assuming nullable based on JSON
    Assertions.assertNull(workout.workoutType, "WorkoutType should be null")
    Assertions.assertNull(workout.workoutStarted, "WorkoutStarted should be null")
    Assertions.assertNull(workout.workoutEnded, "WorkoutEnded should be null")
    Assertions.assertEquals(
      0.0,
      workout.totalVolumeInPounds,
      "TotalVolumeInPounds should be default"
    )
    Assertions.assertEquals(
      0.0,
      workout.totalVolumeInKilograms,
      "TotalVolumeInKilograms should be default"
    )
    Assertions.assertNull(workout.order, "Order should be null")


    // --- Verify nested objects (WorkoutItems) ---
    Assertions.assertFalse(workout.workoutItems.isEmpty(), "WorkoutItems list should not be empty")
    // Add more specific checks for workoutItems if needed, e.g., checking the first item's ID
    Assertions.assertEquals(
      "6779fb71b779f36f007c72c6",
      workout.workoutItems.firstOrNull()?.id.toString(),
      "First WorkoutItem ID should match"
    )
    Assertions.assertEquals(
      "64f0de3ee93f8b251df4654b", when (workout.workoutItems.firstOrNull()) {
        is ExerciseSets -> (workout.workoutItems.firstOrNull() as ExerciseSets).exerciseID.toString()
        else -> null
      }, "First WorkoutItem ExerciseID should match"
    )

    println(workout.workoutItems.toString())
  }

  @OptIn(ExperimentalUuidApi::class)
  @Example
  fun test2_WorkoutDeserializationFromJson() {
    // JSON string from the uploaded file example.txt [cite: 1]
    // NOTE: Removed "_default" and "Workout" top-level keys as they seem meta/incorrect JSON structure
    // for deserializing directly into the Workout object. Ensure your actual JSON source
    // *only* contains the Workout object's representation.
    val workoutJson =
      File("/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/test/java/com/example/myapplication/deserialization/query_results.txt").readText() // [cite: 1] (structure adapted)

    // Deserialize the JSON string to a Workout object
    val workout = json.decodeFromString<WorkoutDao>(workoutJson).Workout

    // Assertions to verify the deserialized object's properties
    assertNotNull(workout, "Workout object should not be null")

    // --- Verify fields based on the JSON [cite: 1] ---
    Assertions.assertEquals(
      "6779fc3723faf50478e954fc",
      workout.id.toString(),
      "ID should match JSON"
    )
    Assertions.assertEquals(
      "06e61930-be71-49c3-8104-0063df599cdf",
      workout.uuid.toString(),
      "UUID should match JSON"
    )

    // name
    Assertions.assertEquals("Day 1 - Upper 1", workout.name, "Name should match JSON")

    // dataCreated
    Assertions.assertEquals(
      Instant.parse("2025-01-01T00:00:00.000000000Z"),
      workout.dateCreated,
      "DateCreated should match JSON"
    )

    // day
    Assertions.assertEquals(1, workout.day, "Day should match JSON")

    // ownerID
    Assertions.assertEquals("RAIDER", workout.ownerID, "OwnerID should match JSON")

    // isPublic
    Assertions.assertEquals(true, workout.isPublic, "isPublic should match JSON")

    // programID
    Assertions.assertEquals(
      "6779fc3723faf50478e954f6",
      workout.programID.toString(),
      "ProgramID should match JSON"
    )

    // mesocycleID
    Assertions.assertEquals(
      "6779fc3723faf50478e954f8",
      workout.mesocycleID.toString(),
      "MesocycleID should match JSON"
    )

    // microcycleID
    Assertions.assertEquals(
      "6779fc3723faf50478e954fa",
      workout.microcycleID.toString(),
      "MicrocycleID should match JSON"
    )

    // lazyListItemID
    Assertions.assertEquals(
      3537540350507381,
      workout.lazyListItemID,
      "LazyListItemID should match JSON"
    )


    // --- Verify fields *not* in the specific JSON snippet [cite: 1] (should be default or null) ---
    Assertions.assertNull(workout.workoutAuthor, "WorkoutAuthor should be null")
    Assertions.assertTrue(workout.notes.isEmpty(), "Notes should be empty")
    Assertions.assertEquals(
      0,
      workout.workoutDuration,
      "WorkoutDuration should be null"
    ) // Assuming nullable based on JSON
    Assertions.assertNull(workout.workoutType, "WorkoutType should be null")
    Assertions.assertNull(workout.workoutStarted, "WorkoutStarted should be null")
    Assertions.assertNull(workout.workoutEnded, "WorkoutEnded should be null")
    Assertions.assertEquals(
      0.0,
      workout.totalVolumeInPounds,
      "TotalVolumeInPounds should be default"
    )
    Assertions.assertEquals(
      0.0,
      workout.totalVolumeInKilograms,
      "TotalVolumeInKilograms should be default"
    )
    Assertions.assertNull(workout.order, "Order should be null")


    // --- Verify nested objects (WorkoutItems) ---
    Assertions.assertFalse(workout.workoutItems.isEmpty(), "WorkoutItems list should not be empty")
    // Add more specific checks for workoutItems if needed, e.g., checking the first item's ID
    Assertions.assertEquals(
      "6779fc3723faf50478e954fe",
      workout.workoutItems.firstOrNull()?.id.toString(),
      "First WorkoutItem ID should match"
    )
    Assertions.assertEquals(
      "64f0de3ee93f8b251df4651d", when (workout.workoutItems.firstOrNull()) {
        is ExerciseSets -> (workout.workoutItems.firstOrNull() as ExerciseSets).exerciseID.toString()
        else -> null
      }, "First WorkoutItem ExerciseID should match"
    )
    Assertions.assertEquals(6, workout.workoutItems.size, "WorkoutItems list size should match")

    println(workout.workoutItems.toString())
  }
}