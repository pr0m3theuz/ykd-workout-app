package com.example.myapplication.data.database

//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.myapplication.data.model.Program
//import kotlinx.coroutines.flow.Flow

//@Dao
//interface ProgramsDao {
//    @Query("SELECT * FROM programs")
//    fun getAllPrograms(): Flow<List<Program>>
//
//    @Query("SELECT * FROM programs WHERE programID=:programID")
//    fun getProgramById(programID: Long): Flow<Program>
//
//    @Insert(onConflict = OnConflictStrategy.ABORT, entity = Program::class)
//    suspend fun insertProgram(program: Program)
//
//    @Update
//    suspend fun updateProgram(program: Program)
//
//    @Delete
//    suspend fun deleteProgram(program: Program)
//}
