package com.example.myapplication.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface ProgramsDao {
  @Query("SELECT * FROM programs")
  fun getAllPrograms(): Flow<List<Program>>

  @Query("SELECT * FROM programs WHERE programID=:programID")
  fun getProgramById(programID: Long): Flow<Program>

  @Insert(onConflict = OnConflictStrategy.ABORT, entity = ExerciseModel::class)
  suspend fun insertProgram(program: Program)

  @Update
  suspend fun updateProgram(program: Program)

  @Delete
  suspend fun deleteProgram(program: Program)
}

interface MesocyclesDao {
  @Query("SELECT * FROM mesocycles")
  suspend fun getAllMesocycles(): List<Mesocycle>

  @Query("SELECT * FROM mesocycles WHERE mesocycleID=:mesocycleID")
  suspend fun getMesocycleById(mesocycleID: Long): Mesocycle?

  @Query("SELECT * FROM microcycles WHERE programID = :programID")
  suspend fun getMesocyclesByProgram(programID: Long): List<Mesocycle>

  @Insert(onConflict = OnConflictStrategy.ABORT, entity = Mesocycle::class)
  suspend fun insertMesocycle(mesocycle: Mesocycle)

  @Update
  suspend fun updateMesocycle(mesocycle: Mesocycle)

  @Delete
  suspend fun deleteMesocycle(mesocycle: Mesocycle)
}
