package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponentListSerializer
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
import kotlin.uuid.ExperimentalUuidApi

class PreviewNextWorkoutTest {
  // Configure kotlinx.serialization Json instance
// It needs to know about custom serializers if they are not contextually available
// Add modules if your serializers require them (e.g., for ObjectId, Uuid if not standard)
  private val json = Json {
    encodeDefaults = true     // If you want to include default values
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
    val previousWorkoutJson =
      File("//mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/assets/previous_workout.json").readText()
    val previousWorkoutComponentsJson =
      File("//mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/assets/previous_workoutComponents.json").readText()
    val nextWorkoutJson =
      File("//mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/assets/next_workout.json").readText()
    val nextWorkoutComponentsJson =
      File("//mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/assets/next_workoutComponents.json").readText()

    // Deserialize the JSON string to a Workout object
    val previousWorkout = json.decodeFromString<Workout>(previousWorkoutJson)
    val previousWorkoutComponents =
      json.decodeFromString(WorkoutComponentListSerializer, previousWorkoutComponentsJson)
    val nextWorkout = json.decodeFromString<Workout>(nextWorkoutJson)
    val nextWorkoutComponents =
      json.decodeFromString(WorkoutComponentListSerializer, nextWorkoutComponentsJson)

    println("Previous Workout: $previousWorkout")
    println("Previous Workout Components: $previousWorkoutComponents")
    println("Next Workout: $nextWorkout")
    println("Next Workout Components: $previousWorkoutComponents")

    // Assertions to verify the deserialized object's properties
    assertNotNull(nextWorkout, "Next Workout object should not be null")
    assertNotNull(nextWorkoutComponents, "Next Workout Components object should not be null")
    assertNotNull(previousWorkout, "Previous Workout object should not be null")
    assertNotNull(
      previousWorkoutComponents,
      "Previous Workout Components object should not be null"
    )
  }
}