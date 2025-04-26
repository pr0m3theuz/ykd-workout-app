package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.Workout
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
import kotlin.uuid.ExperimentalUuidApi

class FindTheNextWorkoutTest {
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
  }
}