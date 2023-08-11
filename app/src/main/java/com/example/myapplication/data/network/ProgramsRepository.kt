package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

class ProgramsRepository constructor(private val database: RealmDatabase) {
    suspend fun migrateData() {
//        database.migrateData()
    }

    fun getAllPrograms(): Flow<List<Program>> {
        return database.getAllPrograms()
    }

    suspend fun getProgramById(programID: ObjectId): Program? {
        return database.getProgramByID(programID)
    }

    suspend fun insertProgram(program: Program) {
        database.insertProgram(program)
    }

    suspend fun updateProgram(program: Program) {
        database.updateProgram(program)
    }

    suspend fun deleteProgram(program: Program) {
        database.deleteProgram(program)
    }

    fun getAllMesocycles(): Flow<List<Mesocycle>> {
        return database.getAllMesocycles()
    }

    fun getMesocycleById(mesocycleID: ObjectId): Mesocycle? {
        return database.getMesocycleByID(mesocycleID)
    }

    fun getMesocyclesByProgram(programID: ObjectId): Flow<List<Mesocycle>> {
        return database.getMesocyclesByProgramID(programID)
    }

    suspend fun insertMesocycle(mesocycle: Mesocycle) {
        database.insertMesocycle(mesocycle)
    }

    suspend fun updateMesocycle(mesocycle: Mesocycle) {
        database.updateMesocycle(mesocycle)
    }

    suspend fun deleteMesocycle(mesocycle: Mesocycle) {
        database.deleteMesocycle(mesocycle)
    }

    fun getAllMicrocycles(): Flow<List<Microcycle>> {
        return database.getAllMicrocycles()
    }

    suspend fun getMicrocycleByID(microcycleID: ObjectId): Microcycle? {
        return database.getMicrocycleByID(microcycleID)
    }

    fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): Flow<List<Microcycle>> {
        return database.getMicrocyclesByMesocycleID(mesocycleID)
    }

    fun getMicrocyclesByProgram(programID: ObjectId): Flow<List<Microcycle>> {
        return database.getMicrocycleByProgramID(programID)
    }

//    suspend fun getMicrocyclesByProgramSorted(programID: Long): List<Microcycle> {
//        return database.getMicrocycleByProgramSorted(programID)
//    }

    suspend fun insertMicrocycle(microcycle: Microcycle) {
        database.insertMicrocycle(microcycle)
    }

    suspend fun updateMicrocycle(microcycle: Microcycle) {
        database.updateMicrocycle(microcycle)
    }

    suspend fun deleteMicrocycle(microcycle: Microcycle) {
        database.deleteMicrocycle(microcycle)
    }
}
