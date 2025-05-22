@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

fun duplicateProgramStructure(
  originalProgram: Program,
  user: UserProfile
): Triple<Program, List<Workout>, List<Set>> {
  val newOwnerIdString = user.id.toHexString()
  val collectedNewWorkouts = mutableListOf<Workout>()
  val collectedNewSets = mutableListOf<Set>()

  // Helper to create and collect new Sets
  fun duplicateAndCollectSet(
    originalSet: Set,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId,
    newMicrocycleId: ObjectId,
    newWorkoutId: ObjectId,
    newExerciseSetsId: ObjectId
  ): Set {
    val newSet = originalSet.copy(
      id = ObjectId(),
      exerciseSetsID = newExerciseSetsId,
      workoutID = newWorkoutId,
      microcycleID = newMicrocycleId,
      mesocycleID = newMesocycleId,
      programID = newProgramId,
      ownerID = newOwnerIdString,
      isPublic = false
    )
    collectedNewSets.add(newSet)
    return newSet // Though this return might not be directly used if parent's list is cleared
  }

  fun duplicateExerciseSets(
    originalEs: ExerciseSets,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId,
    newMicrocycleId: ObjectId,
    newWorkoutId: ObjectId
  ): ExerciseSets {
    val newEsId = ObjectId()
    originalEs.sets.forEach { set -> // Iterate original sets to create new ones for collection
      duplicateAndCollectSet(
        set,
        newProgramId,
        newMesocycleId,
        newMicrocycleId,
        newWorkoutId,
        newEsId
      )
    }
    return originalEs.copy(
      id = newEsId,
      workoutID = newWorkoutId,
      microcycleID = newMicrocycleId,
      mesocycleID = newMesocycleId,
      programID = newProgramId,
      ownerID = newOwnerIdString,
      isPublic = false,
      sets = mutableListOf() // New ExerciseSets has an empty list of sets
    )
  }

  fun duplicateSuperset(
    originalSuperset: Superset,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId,
    newMicrocycleId: ObjectId,
    newWorkoutId: ObjectId
  ): Superset {
    val newSupersetId = ObjectId()
    val newNestedExerciseSets = originalSuperset.exercisesSets.map { nestedEs ->
      // Recursively duplicate ExerciseSets within the Superset.
      // These will also have their 'sets' list emptied, and their original sets collected.
      duplicateExerciseSets(
        nestedEs,
        newProgramId,
        newMesocycleId,
        newMicrocycleId,
        newWorkoutId /*, parentSupersetId = newSupersetId if needed */
      )
    }
    return originalSuperset.copy(
      id = newSupersetId,
      workoutID = newWorkoutId,
      microcycleID = newMicrocycleId,
      mesocycleID = newMesocycleId,
      programID = newProgramId,
      ownerID = newOwnerIdString,
      isPublic = false,
      exercisesSets = newNestedExerciseSets
    )
  }

  fun duplicateWorkoutItem(
    originalItem: WorkoutItem,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId,
    newMicrocycleId: ObjectId,
    newWorkoutId: ObjectId
  ): WorkoutItem {
    return when (originalItem) {
      is ExerciseSets -> duplicateExerciseSets(
        originalItem,
        newProgramId,
        newMesocycleId,
        newMicrocycleId,
        newWorkoutId
      )

      is Superset -> duplicateSuperset(
        originalItem,
        newProgramId,
        newMesocycleId,
        newMicrocycleId,
        newWorkoutId
      )
      // Add other WorkoutItem types if they exist
    }
  }

  fun duplicateAndCollectWorkout(
    originalWorkout: Workout,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId,
    newMicrocycleId: ObjectId
  ): Workout {
    val newWorkoutId = ObjectId()

    val newWorkout = originalWorkout.copy(
      id = newWorkoutId,
      microcycleID = newMicrocycleId,
      mesocycleID = newMesocycleId,
      programID = newProgramId,
      ownerID = newOwnerIdString,
      isPublic = false,
      workoutItems = originalWorkout.workoutItems.map { item ->
        duplicateWorkoutItem(item, newProgramId, newMesocycleId, newMicrocycleId, newWorkoutId)
      }
    )
    collectedNewWorkouts.add(newWorkout)

    return newWorkout
  }

  fun duplicateMicrocycle(
    originalMicrocycle: Microcycle,
    newProgramId: ObjectId,
    newMesocycleId: ObjectId
  ): Microcycle {
    val newMicrocycleId = ObjectId()

    originalMicrocycle.workouts.map { workout ->
      duplicateAndCollectWorkout(workout, newProgramId, newMesocycleId, newMicrocycleId)
    }

    return originalMicrocycle.copy(
      id = newMicrocycleId,
      programID = newProgramId,
      mesocycleID = newMesocycleId,
      ownerID = newOwnerIdString,
      isPublic = false,
      workouts = emptyList() // New Microcycle has an empty list of workouts
    )
  }

  fun duplicateMesocycle(
    originalMesocycle: Mesocycle,
    newProgramId: ObjectId
  ): Mesocycle {
    val newMesocycleId = ObjectId()
    return originalMesocycle.copy(
      id = newMesocycleId,
      programID = newProgramId,
      ownerID = newOwnerIdString,
      isPublic = false,
      microcycles = originalMesocycle.microcycles.map { micro ->
        duplicateMicrocycle(micro, newProgramId, newMesocycleId)
      }
    )
  }

  val newProgramId = ObjectId() // Generate the ID for the new program first
  val newProgram = originalProgram.copy(
    id = newProgramId, // Assign the new ID
    isActive = true,
    isPublic = false,
    ownerID = newOwnerIdString,
    mesocycles = originalProgram.mesocycles.map { mesocycle ->
      duplicateMesocycle(mesocycle, newProgramId)
    }
  )

  return Triple(newProgram, collectedNewWorkouts, collectedNewSets)
}