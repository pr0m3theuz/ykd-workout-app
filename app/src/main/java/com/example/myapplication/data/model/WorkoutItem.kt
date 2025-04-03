@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.model

import com.example.myapplication.utils.newLazyListItemID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO Figure out WorkoutItem using ExerciseExerciseSets

@Serializable
sealed class WorkoutItem {
  abstract val order: Int
}

@Serializable
sealed class WorkoutComponent {
  abstract val id: ObjectId
  abstract val lazyListItemID: Long

  //  abstract val uuid: Uuid
  abstract val order: Int
  abstract val ownerID: String?
  abstract val isPublic: Boolean
}

@Serializable
class ListWorkoutComponent() {
  var list: List<WorkoutComponent> = emptyList()
}

@Serializable
data class Superset(
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var name: String? = null,
  var exercisesSets: List<ExerciseSets> = emptyList(),
  // TODO Add check to override exercise rest durations
  // TODO At creation, request rest durations from user
  var restBetweenExercises: Long = 0L, // Rest time between exercises in milliseconds
  var enableRestBetweenExercises: Boolean = false,
  var enableRestAfterSuperset: Boolean = false,
  var restAfterSuperset: Long = 0L, // Rest time after all exercises in superset are completed
  var supersetNotes: List<SupersetNote> = emptyList(),
  override val order: Int = 1, // Order within workout
  var owerID: String? = null,
  var isPublic: Boolean = false,
) : WorkoutItem()

@Serializable
data class SupersetExercisesSets(
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class) override var id: ObjectId = ObjectId(),
  override var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var name: String? = null,
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
// TODO val backgroundColor:  = IBMColorBlindPalette.random()
) : WorkoutComponent()
