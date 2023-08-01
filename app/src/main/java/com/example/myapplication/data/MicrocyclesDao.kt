package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MicrocyclesDao {
    @Query("SELECT * FROM microcycles")
    suspend fun getAllMicrocycles(): List<Microcycle>

    @Query("SELECT * FROM microcycles WHERE microcycleID = :microcycleID")
    suspend fun getMicrocycleById(microcycleID: Long): Microcycle?

    @Query("SELECT * FROM microcycles WHERE mesocycleID = :mesocycleID")
    suspend fun getMicrocyclesByMesocycle(mesocycleID: Long): List<Microcycle>

    @Query("SELECT * FROM microcycles WHERE programID = :programID")
    suspend fun getMicrocyclesByProgram(programID: Long): List<Microcycle>

    @Query("SELECT * FROM microcycles WHERE programID = :programID ORDER BY dateCreated")
    suspend fun getMicrocyclesByProgramSorted(programID: Long): List<Microcycle>

    @Insert(onConflict = OnConflictStrategy.ABORT, entity = Microcycle::class)
    suspend fun insertMicrocycle(microcycle: Microcycle)

    @Update
    suspend fun updateMicrocycle(microcycle: Microcycle)

    @Delete
    suspend fun deleteMicrocycle(microcycle: Microcycle)
}