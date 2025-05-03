@file:OptIn(ExperimentalSerializationApi::class)

package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.Workout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.serializer
import net.jqwik.api.Example
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
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
    File(filepath).bufferedReader().readLines().mapNotNull map@{
      var workout: Workout? = null
      try {
        workout = json.decodeFromString<Workout>(it)
//        println("Workout: ${workout.id} sucessfully decoded")
        //      workout.signature = generateWorkoutSignature(workout, includeTargets = true)
        //      workout.signatureHash =
        //        MessageDigest.getInstance("SHA-256").digest(workout.signature!!.toByteArray(Charsets.UTF_8))
        //          .toHexString()
      } catch (e: Exception) {
        println("Error processing document ID : ${e.message}")
        println(it)
        e.printStackTrace() // Log the full stack trace for debugging
      } // [cite: 1] (structure adapted)
      return@map workout
    }

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
    string.append("${workouts.first { workout -> workout.ownerID == "MAX_MUSCLE" }.hashedSignature}\n")
    string.append("Max Muscle workout signature hash: ")
    val maxMuscleWorkoutsGroupedBySignatureHash =
      workouts.filter { workout -> workout.ownerID == "MAX_MUSCLE" }
        .groupBy { workout -> workout.hashedSignature }
    string.append("keys size: ${maxMuscleWorkoutsGroupedBySignatureHash.keys.size}\n")
    maxMuscleWorkoutsGroupedBySignatureHash.forEach { string.append("key ${it.key} value size: ${it.value.size}\n") }

    string.appendLine()
    string.append("Number of Raider workouts: ${workouts.filter { workout -> workout.ownerID == "RAIDER" }.size}\n")
    string.append("Raider workout signature: ")
    string.append("${workouts.first { workout -> workout.ownerID == "RAIDER" }.signature}\n")
    string.append("Raider workout signature hash: ")
    string.append("${workouts.first { workout -> workout.ownerID == "RAIDER" }.hashedSignature}\n")
    string.append("Raider workout signature hash: ")
    val raiderWorkoutsGroupedBySignatureHash =
      workouts.filter { workout -> workout.ownerID == "RAIDER" }
        .groupBy { workout -> workout.hashedSignature }
    string.append("keys size: ${raiderWorkoutsGroupedBySignatureHash.keys.size}\n")
    raiderWorkoutsGroupedBySignatureHash.forEach { string.append("key ${it.key} value size: ${it.value.size}\n") }

    println(string)
  }

  @Example
  fun testIdentifyIdenticalWorkouts() {
    val string = StringBuilder()
    val mapMesocycleMicrocycleWorkout: Map<ObjectId?, Map<ObjectId?, ObjectId>> =
      workouts.groupBy { it.mesocycleID }
        .mapValues { (_, workoutsInMesocycle) ->
          workoutsInMesocycle.associate { it.microcycleID to it.id }
        }

    val mesocycles = mutableListOf<ObjectId?>()
    workouts.filter { workout -> workout.ownerID == "RAIDER" }.distinctBy { it.mesocycleID }
      .forEach { mesocycles.add(it.mesocycleID) }

    val microcycleWorkouts = mutableMapOf<ObjectId?, MutableList<ObjectId?>>()
    workouts.forEach { workout ->
      microcycleWorkouts.getOrPut(workout.microcycleID) { mutableListOf() }.add(workout.id)
    }

//    val complexComparator = compareBy<Pair<Int, String?>>({workout.microcycleID}, {workout.mesocycleID})


    mesocycles.forEach { mesocycle ->
      string.append("mesocycle: $mesocycle\n")
      workouts.filter { workout -> workout.mesocycleID == mesocycle }.forEach { workout ->
        string.append("\t\tmicrocycle: ${workout.microcycleID}")
        string.append("\t\tworkouts: ${microcycleWorkouts[workout.microcycleID]}\n")
      }
    }

    println(string)
  }

  @Example
  fun testIdentifyIdenticalWorkoutsFunctional() {
    // 1. Create a map of Microcycle ID to a list of all Workout IDs associated with it.
    // This groups all workouts globally by their microcycle.
    val allWorkoutIdsByMicrocycleId: Map<ObjectId, List<ObjectId>> = workouts
      .filter { it.microcycleID != null } // Ensure microcycleID is not null
      .groupBy { it.microcycleID!! } // Group by non-null microcycle ID
      .mapValues { (_, workoutsInMicrocycle) -> // For each microcycle group
        workoutsInMicrocycle.map { it.id } // Extract the list of workout IDs
      }

    // 2. Process RAIDER's workouts to generate the output string.
    val outputString = workouts
      .filter { it.ownerID == "RAIDER" && it.mesocycleID != null } // Filter for RAIDER's workouts with non-null mesocycle IDs
      .groupBy { it.mesocycleID!! } // Group them by non-null mesocycle ID
      .map { (mesocycleId, raiderWorkoutsInMesocycle) -> // Process each mesocycle group (mesocycleId -> List<Workout>)
        // Start the string block for this mesocycle
        val mesocycleHeader = "mesocycle: $mesocycleId\n"

        // Process the microcycles within this mesocycle for RAIDER
        val microcycleLines = raiderWorkoutsInMesocycle
          .mapNotNull { it.microcycleID } // Get non-null microcycle IDs for RAIDER's workouts in this meso
          .distinct() // Consider each unique microcycle ID only once per mesocycle
          .sorted() // Optional: Sort microcycle IDs for consistent output order
          .map { microcycleId -> // For each distinct microcycle ID
            // Fetch the list of *all* workout IDs associated with this microcycle
            // from the map created in step 1. Default to empty list if not found.
            val workoutIdsForThisMicrocycle =
              allWorkoutIdsByMicrocycleId[microcycleId] ?: emptyList()
            // Format the output line for this microcycle
            "\t\tmicrocycle: $microcycleId\t\tworkouts: $workoutIdsForThisMicrocycle\n"
          }.joinToString(separator = "") // Join the lines for all microcycles within this mesocycle

        // Combine the mesocycle header and the microcycle lines
        mesocycleHeader + microcycleLines
      }
      .sorted() // Optional: Sort mesocycles by ID for consistent output order
      .joinToString(separator = "") // Join the string blocks for all mesocycles

    // Print the final result
    println(outputString)

    /* Expected output based on sample data (order might vary without sorting):
       mesocycle: m1
           microcycle: mc1        workouts: [w1, w3, w7]
           microcycle: mc2        workouts: [w2, w5]
       mesocycle: m2
           microcycle: mc3        workouts: [w4]
    */
  }

  object KotlinxGenericMapSerializer : KSerializer<Map<String, Any?>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GenericMap")

    override fun serialize(encoder: Encoder, value: Map<String, Any?>) {
      val jsonObject = JsonObject(value.mapValues { it.value.toJsonElement() })
      val jsonObjectSerializer = encoder.serializersModule.serializer<JsonObject>()
      jsonObjectSerializer.serialize(encoder, jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
      val jsonDecoder = decoder as? JsonDecoder
        ?: throw SerializationException("Can only deserialize Json content to generic Map")
      val root = jsonDecoder.decodeJsonElement()
      return if (root is JsonObject) root.toMap() else throw SerializationException("Cannot deserialize Json content to generic Map")
    }

    private fun Any?.toJsonElement(): JsonElement = when (this) {
      null -> JsonNull
      is String -> JsonPrimitive(this)
      is Number -> JsonPrimitive(this)
      is Boolean -> JsonPrimitive(this)
      is Map<*, *> -> toJsonObject()
      is Iterable<*> -> toJsonArray()
      else -> throw SerializationException("Cannot serialize value type $this")
    }

    private fun Map<*, *>.toJsonObject(): JsonObject =
      JsonObject(this.entries.associate { it.key.toString() to it.value.toJsonElement() })

    private fun Iterable<*>.toJsonArray(): JsonArray = JsonArray(this.map { it.toJsonElement() })

    private fun JsonElement.toAnyNullableValue(): Any? = when (this) {
      is JsonPrimitive -> toScalarOrNull()
      is JsonObject -> toMap()
      is JsonArray -> toList()
    }

    private fun JsonObject.toMap(): Map<String, Any?> = entries.associate {
      when (val jsonElement = it.value) {
        is JsonPrimitive -> it.key to jsonElement.toScalarOrNull()
        is JsonObject -> it.key to jsonElement.toMap()
        is JsonArray -> it.key to jsonElement.toAnyNullableValueList()
      }
    }

    private fun JsonPrimitive.toScalarOrNull(): Any? = when {
      this is JsonNull -> null
      this.isString -> this.content
      else -> listOfNotNull(booleanOrNull, longOrNull, doubleOrNull).firstOrNull()
    }

    private fun JsonArray.toAnyNullableValueList(): List<Any?> = this.map {
      it.toAnyNullableValue()
    }
  }
}