@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import kotlin.uuid.ExperimentalUuidApi


class ImportWorkoutProgramRepository constructor(private val database: RealmDatabase) {
  suspend fun importWorkoutProgramPKL(program: Program) {
    database.importWorkoutProgramPKL(program)
  }

  suspend fun importMesocyclesPKL(mesocycles: List<Mesocycle>) {
    database.importMesocyclesPKL(mesocycles)
  }

  suspend fun importMicrocyclesPKL(microcycles: List<Microcycle>) {
    database.importMicrocyclesPKL(microcycles)
  }

  suspend fun importWorkoutsPKL(workouts: List<Workout>) {
    database.importWorkoutsPKL(workouts)
  }

  suspend fun importExercisesSetsPKL(exercisesSets: List<ExerciseSets>) {
    database.importExercisesSetsPKL(exercisesSets)
  }

  suspend fun importSetsPKL(sets: List<Set>) {
    database.importSetsPKL(sets)
  }
}
