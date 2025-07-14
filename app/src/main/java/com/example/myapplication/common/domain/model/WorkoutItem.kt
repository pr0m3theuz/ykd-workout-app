@file:UseSerializers(InstantSerializer::class, UuidSerializer::class, ObjectIdSerializer::class)
@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.InstantSerializer
import com.example.myapplication.common.util.ObjectIdSerializer
import com.example.myapplication.common.util.UuidSerializer
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO Figure out WorkoutItem using ExerciseExerciseSets

object WorkoutItemSerializer : JsonContentPolymorphicSerializer<WorkoutItem>(WorkoutItem::class) {
  override fun selectDeserializer(element: JsonElement) = when {
    "exercisesSets" in element.jsonObject -> Superset.serializer()
    "exerciseID" in element.jsonObject -> ExerciseSets.serializer()
    // Fallback or error handling if neither key is present
    else -> throw SerializationException("Unknown WorkoutItem type: Cannot determine between Superset and ExerciseSets for element: $element")
    // Or, if ExerciseSets is the default/fallback:
    // else -> ExerciseSets.serializer()
  }
}

object WorkoutComponentSerializer :
  JsonContentPolymorphicSerializer<WorkoutComponent>(WorkoutComponent::class) {
  override fun selectDeserializer(element: JsonElement) = when {
    "exercisesSets" in element.jsonObject -> SupersetExercisesSets.serializer()
    "exercise" in element.jsonObject -> ExerciseExerciseSets.serializer()
    // Fallback or error handling if neither key is present
    else -> throw SerializationException("Unknown WorkoutItem type: Cannot determine between Superset and ExerciseSets for element: $element")
    // Or, if ExerciseSets is the default/fallback:
    // else -> ExerciseSets.serializer()
  }
}

object WorkoutItemListSerializer : KSerializer<List<WorkoutItem>> {
  override val descriptor = WorkoutItemSerializer.descriptor

  override fun deserialize(decoder: Decoder): List<WorkoutItem> {
    val list = decoder.decodeSerializableValue(ListSerializer(WorkoutItemSerializer))
    return list.sortedBy { it.order }
  }

  override fun serialize(encoder: Encoder, value: List<WorkoutItem>) {
    val list = value.sortedBy { it.order }
    return encoder.encodeSerializableValue(ListSerializer(WorkoutItemSerializer), list)
  }
}

object WorkoutComponentListSerializer : KSerializer<List<WorkoutComponent>> {
  override val descriptor = WorkoutItemSerializer.descriptor

  override fun deserialize(decoder: Decoder): List<WorkoutComponent> {
    val list = decoder.decodeSerializableValue(ListSerializer(WorkoutComponentSerializer))
    return list.sortedBy { it.order }
  }

  override fun serialize(encoder: Encoder, value: List<WorkoutComponent>) {
    val list = value.sortedBy { it.order }
    return encoder.encodeSerializableValue(ListSerializer(WorkoutComponentSerializer), list)
  }
}



@Serializable
data class WorkoutItemDao(val WorkoutItem: WorkoutItem)

@Serializable
sealed class WorkoutItem {
  abstract var id: ObjectId
  abstract val order: Int
  abstract val ownerID: String?
  abstract val isPublic: Boolean
  abstract val type: String
}

@Serializable
sealed class WorkoutComponent {
  abstract val id: ObjectId
  abstract val lazyListItemID: Long

  //  abstract val uuid: Uuid
  abstract val order: Int
  abstract val ownerID: String?
  abstract val isPublic: Boolean
  abstract val type: String
}

@Serializable
class ListWorkoutComponent() {
  var list: List<WorkoutComponent> = emptyList()
}

@Serializable
data class Superset(
  @SerialName("id")
  override var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  var uuid: Uuid = Uuid.random(),
//  var name: String? = null,
  var exercisesSets: List<ExerciseSets> = emptyList(),
  // TODO Add check to override exercise rest durations
  // TODO At creation, request rest durations from user
  var restBetweenExercises: Long = 0L, // Rest time between exercises in milliseconds
  var enableRestBetweenExercises: Boolean = false,
  var enableRestAfterSuperset: Boolean = false,
  var restAfterSuperset: Long = 0L, // Rest time after all exercises in superset are completed
  var supersetNotes: List<SupersetNote> = emptyList(),
  override val order: Int = 1, // Order within workout
  override var ownerID: String?,
  override var isPublic: Boolean = false,
  override val type: String = "Superset",
  var workoutID: ObjectId, // External Primary Key
  var microcycleID: ObjectId?,
  var mesocycleID: ObjectId?,
  var programID: ObjectId?,
) : WorkoutItem()

@Serializable
data class SupersetExercisesSets(
  @SerialName("id")
  override var id: ObjectId = ObjectId(),
  override var lazyListItemID: Long = newLazyListItemID(),
  var uuid: Uuid = Uuid.random(),
//  var name: String? = null,
  var exercisesSets: List<ExerciseExerciseSets> = emptyList(),
  // TODO Add check to override exercise rest durations
  // TODO At creation, request rest durations from user
  var restBetweenExercises: Long = 0L, // Rest time between exercises in milliseconds
  var enableRestBetweenExercises: Boolean = false,
  var enableRestAfterSuperset: Boolean = false,
  var restAfterSuperset: Long = 0L, // Rest time after all exercises in superset are completed
  var supersetNotes: List<SupersetNote> = emptyList(), // TODO Create SupersetNote class
  override var order: Int = 1, // Order within workout
  override val ownerID: String? = null,
  override var isPublic: Boolean = false,
  @SerialName("workoutComponentType") override val type: String = "SupersetExercisesSets",
  val workoutID: ObjectId, // External Primary Key
  val microcycleID: ObjectId?,
  val mesocycleID: ObjectId?,
  val programID: ObjectId?,
) : WorkoutComponent()
