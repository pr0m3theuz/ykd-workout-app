package com.example.myapplication.data.database

//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.myapplication.data.model.Mesocycle

//@Dao
//interface MesocyclesDao {
//    @Query("SELECT * FROM mesocycles")
//    suspend fun getAllMesocycles(): List<Mesocycle>
//
//    @Query("SELECT * FROM mesocycles WHERE mesocycleID=:mesocycleID")
//    suspend fun getMesocycleById(mesocycleID: Long): Mesocycle?
//
//    @Query("SELECT * FROM microcycles WHERE programID = :programID")
//    suspend fun getMesocyclesByProgram(programID: Long): List<Mesocycle>
//
//    @Insert(onConflict = OnConflictStrategy.ABORT, entity = Mesocycle::class)
//    suspend fun insertMesocycle(mesocycle: Mesocycle)
//
//    @Update
//    suspend fun updateMesocycle(mesocycle: Mesocycle)
//
//    @Delete
//    suspend fun deleteMesocycle(mesocycle: Mesocycle)
//}
