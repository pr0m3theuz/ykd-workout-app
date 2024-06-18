package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import io.realm.kotlin.types.RealmList

class ImportWorkoutProgramRepository constructor(private val database: RealmDatabase) {
  suspend fun importWorkoutProgramPKL(program: Program) {
    database.importWorkoutProgramPKL(program)
  }

  suspend fun importMesocyclesPKL(mesocycles: RealmList<Mesocycle>) {
    database.importMesocyclesPKL(mesocycles)
  }

  suspend fun importMicrocyclesPKL(microcycles: RealmList<Microcycle>) {
    database.importMicrocyclesPKL(microcycles)
  }

  suspend fun importWorkoutsPKL(workouts: RealmList<Workout>) {
    database.importWorkoutsPKL(workouts)
  }

  suspend fun importExercisesSetsPKL(exercisesSets: RealmList<ExerciseSets>) {
    database.importExercisesSetsPKL(exercisesSets)
  }

  suspend fun importSetsPKL(sets: RealmList<Set>) {
    database.importSetsPKL(sets)
  }
}
