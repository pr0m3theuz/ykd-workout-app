@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
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

  fun getProgramById(programID: ObjectId): Program?

  fun getActivePrograms(): Flow<List<Program>>

  fun isProgramActive(programUUID: Uuid): Boolean

  suspend fun copyProgramFromTemplate(program: Program): Boolean

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

  fun getNextWorkoutByProgramID(programID: ObjectId): Workout?
}
