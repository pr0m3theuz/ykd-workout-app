@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ProgramsRepository {
  suspend fun migrateData() {
    //        database.migrateData()
    //        database.migrateMesocycles()
    //        database.migrateWorkouts()
    //        database.migrateExercises()
//            database.importExerciseSets()
  }

  fun getAllDistinctProgramTemplates(): Flow<List<Program>>

  fun getProgramByID(programID: ObjectId): Program?

  fun getActiveProgramByID(programID: ObjectId): Program?

  fun getActivePrograms(): Flow<List<Program>>

  fun isProgramActive(programUUID: Uuid): Boolean

  fun isProgramActiveByID(programID: String): Boolean

  suspend fun createActiveProgram(program: Program): Boolean

  suspend fun insertProgram(program: Program): Boolean

  suspend fun updateProgram(program: Program): Boolean

  suspend fun deleteProgram(program: Program): Boolean

  suspend fun deleteActiveProgram(program: Program): Boolean

  fun getAllMesocycles(): Flow<List<Mesocycle>>

  fun getMesocycleById(mesocycleID: ObjectId): Mesocycle?

  fun getMesocyclesByProgram(programID: ObjectId): Flow<List<Mesocycle>>

  suspend fun insertMesocycle(mesocycle: Mesocycle): Boolean

  suspend fun updateMesocycle(mesocycle: Mesocycle): Boolean

  suspend fun deleteMesocycle(mesocycle: Mesocycle): Boolean

  fun getAllMicrocycles(): Flow<List<Microcycle>>

  suspend fun getMicrocycleByID(microcycleID: ObjectId): Microcycle?

  fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): Flow<List<Microcycle>>

  fun getMicrocyclesByProgram(programID: ObjectId): Flow<List<Microcycle>>

  //    suspend fun getMicrocyclesByProgramSorted(programID: Long): List<Microcycle> {
  //        return database.getMicrocycleByProgramSorted(programID)
  //    }

  suspend fun insertMicrocycle(microcycle: Microcycle): Boolean

  suspend fun updateMicrocycle(microcycle: Microcycle): Boolean

  suspend fun deleteMicrocycle(microcycle: Microcycle): Boolean

  //  @Deprecated("Current Implementation is inadequate for getting next workout", replaceWith = ReplaceWith("WorkoutRepository.getNextWorkoutInProgram()")
  fun getNextWorkoutByProgramID(programID: ObjectId): Workout?
}
