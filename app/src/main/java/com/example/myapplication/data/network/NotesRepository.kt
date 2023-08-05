package com.example.myapplication.data.network

import androidx.annotation.WorkerThread
import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.WorkoutNote
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

class ExerciseNotesRepository(private val database: RealmDatabase) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
//    fun getExerciseNotes(exerciseID: ObjectId): Flow<List<ExerciseNote>> =
//        database.getExerciseNotes()

//    fun getExerciseNotesforWorkout(exerciseID: Long, workoutID: Long): Flow<List<ExerciseNote>> =
//        database.getExerciseNotesforWorkout(exerciseID, workoutID)

    fun getPinnedExerciseNotes(
        exerciseID: ObjectId,
    ): Flow<List<ExerciseNote>> =
        database.getPinnedExerciseNotes(exerciseID)

//    fun getExerciseNotesforMicrocycle(
//        exerciseID: Long,
//        microcycleID: Long,
//    ): Flow<List<ExerciseNote>> =
//        database.getExerciseNotesforMicrocycle(exerciseID, microcycleID)
//
//    fun getExerciseNotesforMesocycle(
//        exerciseID: Long,
//        mesocycleID: Long,
//    ): Flow<List<ExerciseNote>> =
//        database.getExerciseNotesforMesocycle(exerciseID, mesocycleID)
//
//    fun getExerciseNotesforProgram(
//        exerciseID: Long,
//        programID: Long,
//    ): Flow<List<ExerciseNote>> = database.getExerciseNotesforProgram(exerciseID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {
        database.insertExerciseNote(exerciseNote)
    }

//    @WorkerThread
//    fun updateInsertExerciseNote(exerciseNote: ExerciseNote) {
//        database.updateInsertExerciseNote(exerciseNote)
//    }
//
//    @WorkerThread
//    fun deleteExerciseNote(exerciseNote: ExerciseNote) {
//        database.deleteExerciseNote(exerciseNote)
//    }
}

class WorkoutNotesRepository(private val database: RealmDatabase) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
//    fun getWorkoutNotes(workoutID: Long): Flow<List<WorkoutNote>> =
//        database.getWorkoutNotes(workoutID)

    fun getPinnedWorkoutNotes(
        workoutID: ObjectId,
    ): Flow<List<WorkoutNote>> =
        database.getPinnedWorkoutNotes(workoutID)

//    fun getWorkoutNotesforMicrocycle(
//        workoutID: Long,
//        microcycleID: Long,
//    ): Flow<List<WorkoutNote>> =
//        database.getWorkoutNotesforMicrocycle(workoutID, microcycleID)
//
//    fun getWorkoutNotesforMesocycle(
//        workoutID: Long,
//        mesocycleID: Long,
//    ): Flow<List<WorkoutNote>> =
//        database.getWorkoutNotesforMesocycle(workoutID, mesocycleID)
//
//    fun getWorkoutNotesforProgram(
//        workoutID: Long,
//        programID: Long,
//    ): Flow<List<WorkoutNote>> = database.getWorkoutNotesforProgram(workoutID, programID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

//    @WorkerThread
//    suspend fun insertWorkoutNote(workoutNote: WorkoutNote) {
//        database.insertWorkoutNote(workoutNote)
//    }
//
//    @WorkerThread
//    suspend fun updateWorkoutNote(workoutNote: WorkoutNote) {
//        database.updateWorkoutNote(workoutNote)
//    }
//
//    @WorkerThread
//    suspend fun deleteWorkoutNote(workoutNote: WorkoutNote) {
//        database.deleteWorkoutNote(workoutNote)
//    }
}


/*

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
*/
