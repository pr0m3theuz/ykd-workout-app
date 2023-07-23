package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramsRepository
@Inject
constructor(
  private val programsDao: ProgramsDao,
  private val mesocyclesDao: MesocyclesDao,
  private val microcyclesDao: MicrocyclesDao
) {
  fun getAllPrograms(): Flow<List<Program>> {
    return programsDao.getAllPrograms()
  }

  fun getProgramById(programID: Long): Flow<Program> {
    return programsDao.getProgramById(programID)
  }

  suspend fun insertProgram(program: Program) {
    programsDao.insertProgram(program)
  }

  suspend fun updateProgram(program: Program) {
    programsDao.updateProgram(program)
  }

  suspend fun deleteProgram(program: Program) {
    programsDao.deleteProgram(program)
  }

  suspend fun getAllMesocycles(): List<Mesocycle> {
    return mesocyclesDao.getAllMesocycles()
  }

  suspend fun getMesocycleById(mesocycleID: Long): Mesocycle? {
    return mesocyclesDao.getMesocycleById(mesocycleID)
  }

  suspend fun getMesocyclesByProgram(programID: Long): List<Mesocycle> {
    return mesocyclesDao.getMesocyclesByProgram(programID)
  }

  suspend fun insertMesocycle(mesocycle: Mesocycle) {
    mesocyclesDao.insertMesocycle(mesocycle)
  }

  suspend fun updateMesocycle(mesocycle: Mesocycle) {
    mesocyclesDao.updateMesocycle(mesocycle)
  }

  suspend fun deleteMesocycle(mesocycle: Mesocycle) {
    mesocyclesDao.deleteMesocycle(mesocycle)
  }

  suspend fun getAllMicrocycles(): List<Microcycle> {
    return microcyclesDao.getAllMicrocycles()
  }

  suspend fun getMicrocycleById(microcycleID: Long): Microcycle? {
    return microcyclesDao.getMicrocycleById(microcycleID)
  }

  suspend fun getMicrocyclesByMesocycle(mesocycleID: Long): List<Microcycle> {
    return microcyclesDao.getMicrocyclesByMesocycle(mesocycleID)
  }

  suspend fun getMicrocyclesByProgram(programID: Long): List<Microcycle> {
    return microcyclesDao.getMicrocyclesByProgram(programID)
  }

  suspend fun insertMicrocycle(microcycle: Microcycle) {
    microcyclesDao.insertMicrocycle(microcycle)
  }

  suspend fun updateMicrocycle(microcycle: Microcycle) {
    microcyclesDao.updateMicrocycle(microcycle)
  }

  suspend fun deleteMicrocycle(microcycle: Microcycle) {
    microcyclesDao.deleteMicrocycle(microcycle)
  }
}
