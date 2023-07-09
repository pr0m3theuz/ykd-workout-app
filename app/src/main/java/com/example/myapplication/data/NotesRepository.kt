package com.example.myapplication.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
@Singleton
class ExerciseNotesRepository @Inject constructor(private val exerciseNotesDao: ExerciseNotesDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getExerciseNotes(exerciseID: Long): Flow<List<ExerciseNote>> =
        exerciseNotesDao.getExerciseNotes(exerciseID)

    fun getExerciseNotesforWorkout(exerciseID: Long, workoutID: Long): Flow<List<ExerciseNote>> =
        exerciseNotesDao.getExerciseNotesforWorkout(exerciseID, workoutID)

    fun getPinnedExerciseNotes(
        exerciseID: Long,
    ): Flow<List<ExerciseNote>> =
        exerciseNotesDao.getPinnedExerciseNotes(exerciseID)

    fun getExerciseNotesforMicrocycle(
        exerciseID: Long,
        microcycleID: Long,
    ): Flow<List<ExerciseNote>> =
        exerciseNotesDao.getExerciseNotesforMicrocycle(exerciseID, microcycleID)

    fun getExerciseNotesforMesocycle(
        exerciseID: Long,
        mesocycleID: Long,
    ): Flow<List<ExerciseNote>> =
        exerciseNotesDao.getExerciseNotesforMesocycle(exerciseID, mesocycleID)

    fun getExerciseNotesforProgram(
        exerciseID: Long,
        programID: Long,
    ): Flow<List<ExerciseNote>> = exerciseNotesDao.getExerciseNotesforProgram(exerciseID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {
        exerciseNotesDao.insertExerciseNote(exerciseNote)
    }

    @WorkerThread
    fun updateInsertExerciseNote(exerciseNote: ExerciseNote) {
        exerciseNotesDao.updateInsertExerciseNote(exerciseNote)
    }

    @WorkerThread
    fun deleteExerciseNote(exerciseNote: ExerciseNote) {
        exerciseNotesDao.deleteExerciseNote(exerciseNote)
    }
}

@Singleton
class WorkoutNotesRepository @Inject constructor(private val workoutNotesDao: WorkoutNotesDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getWorkoutNotes(workoutID: Long): Flow<List<WorkoutNote>> =
        workoutNotesDao.getWorkoutNotes(workoutID)

    fun getPinnedWorkoutNotes(
        workoutID: Long,
    ): Flow<List<WorkoutNote>> =
        workoutNotesDao.getPinnedWorkoutNotes(workoutID)

    fun getWorkoutNotesforMicrocycle(
        workoutID: Long,
        microcycleID: Long,
    ): Flow<List<WorkoutNote>> =
        workoutNotesDao.getWorkoutNotesforMicrocycle(workoutID, microcycleID)

    fun getWorkoutNotesforMesocycle(
        workoutID: Long,
        mesocycleID: Long,
    ): Flow<List<WorkoutNote>> =
        workoutNotesDao.getWorkoutNotesforMesocycle(workoutID, mesocycleID)

    fun getWorkoutNotesforProgram(
        workoutID: Long,
        programID: Long,
    ): Flow<List<WorkoutNote>> = workoutNotesDao.getWorkoutNotesforProgram(workoutID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertWorkoutNote(workoutNote: WorkoutNote) {
        workoutNotesDao.insertWorkoutNote(workoutNote)
    }

    @WorkerThread
    suspend fun updateWorkoutNote(workoutNote: WorkoutNote) {
        workoutNotesDao.updateWorkoutNote(workoutNote)
    }

    @WorkerThread
    suspend fun deleteWorkoutNote(workoutNote: WorkoutNote) {
        workoutNotesDao.deleteWorkoutNote(workoutNote)
    }
}

@Singleton
class MicrocycleNotesRepository @Inject constructor(private val microcycleNotesDao: MicrocycleNotesDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getMicrocycleNotes(microcycleID: Long): Flow<List<MicrocycleNote>> =
        microcycleNotesDao.getMicrocycleNotes(microcycleID)

    fun getPinnedMicrocycleNotes(
        microcycleID: Long,
    ): Flow<List<MicrocycleNote>> =
        microcycleNotesDao.getPinnedMicrocycleNotes(microcycleID)

    fun getMicrocycleNotesforMesocycle(
        microcycleID: Long,
        mesocycleID: Long,
    ): Flow<List<MicrocycleNote>> =
        microcycleNotesDao.getMicrocycleNotesforMesocycle(microcycleID, mesocycleID)

    fun getMicrocycleNotesforProgram(
        microcycleID: Long,
        programID: Long,
    ): Flow<List<MicrocycleNote>> =
        microcycleNotesDao.getMicrocycleNotesforProgram(microcycleID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertMicrocycleNote(microcycleNote: MicrocycleNote) {
        microcycleNotesDao.insertMicrocycleNote(microcycleNote)
    }

    @WorkerThread
    suspend fun updateMicrocycleNote(microcycleNote: MicrocycleNote) {
        microcycleNotesDao.updateMicrocycleNote(microcycleNote)
    }

    @WorkerThread
    suspend fun deleteMicrocycleNote(microcycleNote: MicrocycleNote) {
        microcycleNotesDao.deleteMicrocycleNote(microcycleNote)
    }
}

@Singleton
class MesocycleNotesRepository @Inject constructor(private val mesocycleNotesDao: MesocycleNotesDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getMesocycleNotes(mesocycleID: Long): Flow<List<MesocycleNote>> =
        mesocycleNotesDao.getMesocycleNotes(mesocycleID)

    fun getPinnedMesocycleNotes(
        mesocycleID: Long,
    ): Flow<List<MesocycleNote>> =
        mesocycleNotesDao.getPinnedMesocycleNotes(mesocycleID)

    fun getMesocycleNotesforProgram(
        mesocycleID: Long,
        programID: Long,
    ): Flow<List<MesocycleNote>> =
        mesocycleNotesDao.getMesocycleNotesforProgram(mesocycleID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertMesocycleNote(mesocycleNote: MesocycleNote) {
        mesocycleNotesDao.insertMesocycleNote(mesocycleNote)
    }

    @WorkerThread
    suspend fun updateMesocycleNote(mesocycleNote: MesocycleNote) {
        mesocycleNotesDao.updateMesocycleNote(mesocycleNote)
    }

    @WorkerThread
    suspend fun deleteMesocycleNote(mesocycleNote: MesocycleNote) {
        mesocycleNotesDao.deleteMesocycleNote(mesocycleNote)
    }
}

@Singleton
class ProgramNotesRepository @Inject constructor(private val programNotesDao: ProgramNotesDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getProgramNotes(programID: Long): Flow<List<ProgramNote>> =
        programNotesDao.getProgramNotes(programID)

    fun getPinnedProgramNotes(
        programID: Long,
    ): Flow<List<ProgramNote>> =
        programNotesDao.getPinnedProgramNotes(programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertProgramNote(programNote: ProgramNote) {
        programNotesDao.insertProgramNote(programNote)
    }

    @WorkerThread
    suspend fun updateProgramNote(programNote: ProgramNote) {
        programNotesDao.updateProgramNote(programNote)
    }

    @WorkerThread
    suspend fun deleteProgramNote(programNote: ProgramNote) {
        programNotesDao.deleteProgramNote(programNote)
    }
}