package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// TODO Switch to use UUIDs instead of IDs
@Dao
interface ExerciseNotesDao {
    @Query("SELECT * FROM exerciseNotes WHERE exerciseID = :exerciseID  ORDER BY dateCreated ASC")
    fun getExerciseNotes(exerciseID: Long): Flow<List<ExerciseNote>>

    @Query(
        "SELECT * FROM exerciseNotes " +
                "WHERE exerciseID = :exerciseID AND pinned = :pinned AND programPinned=:programPinned " +
                "AND mesocyclePinned = :mesocyclePinned AND microcyclePinned=:microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getPinnedExerciseNotes(
        exerciseID: Long,
        pinned: Boolean = true,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = false,
    ): Flow<List<ExerciseNote>>

    @Query("SELECT * FROM exerciseNotes WHERE exerciseID = :exerciseID AND workoutID = :workoutID ORDER BY id ASC")
    fun getExerciseNotesforWorkout(exerciseID: Long, workoutID: Long): Flow<List<ExerciseNote>>

    @Query(
        "SELECT * FROM exerciseNotes " +
                "WHERE exerciseID = :exerciseID AND microcycleID = :microcycleID AND programPinned=:programPinned " +
                "AND mesocyclePinned = :mesocyclePinned AND microcyclePinned=:microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getExerciseNotesforMicrocycle(
        exerciseID: Long,
        microcycleID: Long,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = true,
    ): Flow<List<ExerciseNote>>

    @Query(
        "SELECT * FROM exerciseNotes " +
                "WHERE exerciseID = :exerciseID AND mesocycleID = :mesocycleID AND programPinned=:programPinned " +
                "AND mesocyclePinned = :mesocyclePinned AND microcyclePinned=:microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getExerciseNotesforMesocycle(
        exerciseID: Long,
        mesocycleID: Long,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = true,
        microcyclePinned: Boolean = false,
    ): Flow<List<ExerciseNote>>

    @Query(
        "SELECT * FROM exerciseNotes " +
                "WHERE exerciseID = :exerciseID AND programID = :programID AND programPinned=:programPinned " +
                "AND mesocyclePinned = :mesocyclePinned AND microcyclePinned=:microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getExerciseNotesforProgram(
        exerciseID: Long,
        programID: Long,
        programPinned: Boolean = true,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = false,
    ): Flow<List<ExerciseNote>>

    @Insert(onConflict = OnConflictStrategy.ABORT, entity = ExerciseNote::class)
    suspend fun insertExerciseNote(exerciseNote: ExerciseNote)

    @Upsert(entity = ExerciseNote::class)
    fun updateInsertExerciseNote(exerciseNote: ExerciseNote)

    @Delete
    fun deleteExerciseNote(exerciseNote: ExerciseNote)
}

@Dao
interface WorkoutNotesDao {
    @Query("SELECT * FROM workoutNotes WHERE workoutID = :workoutID ORDER BY id ASC")
    fun getWorkoutNotes(workoutID: Long): Flow<List<WorkoutNote>>

    @Query(
        "SELECT * FROM workoutNotes " +
                "WHERE workoutID = :workoutID AND pinned = :pinned " +
                "AND programPinned = :programPinned AND mesocyclePinned = :mesocyclePinned " +
                "AND microcyclePinned = :microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getPinnedWorkoutNotes(
        workoutID: Long,
        pinned: Boolean = true,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = false,
    ): Flow<List<WorkoutNote>>

    @Query(
        "SELECT * FROM workoutNotes " +
                "WHERE workoutID = :workoutID AND microcycleID=:microcycleID " +
                "AND programPinned = :programPinned AND mesocyclePinned=:mesocyclePinned " +
                "AND microcyclePinned = :microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getWorkoutNotesforMicrocycle(
        workoutID: Long,
        microcycleID: Long,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = true,
    ): Flow<List<WorkoutNote>>

    @Query(
        "SELECT * FROM workoutNotes " +
                "WHERE workoutID = :workoutID AND mesocycleID=:mesocycleID " +
                "AND programPinned = :programPinned AND mesocyclePinned=:mesocyclePinned " +
                "AND microcyclePinned = :microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getWorkoutNotesforMesocycle(
        workoutID: Long,
        mesocycleID: Long,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = true,
        microcyclePinned: Boolean = false,
    ): Flow<List<WorkoutNote>>

    @Query(
        "SELECT * FROM workoutNotes " +
                "WHERE workoutID = :workoutID AND programID=:programID " +
                "AND programPinned = :programPinned AND mesocyclePinned=:mesocyclePinned " +
                "AND microcyclePinned = :microcyclePinned " +
                "ORDER BY id ASC"
    )
    fun getWorkoutNotesforProgram(
        workoutID: Long,
        programID: Long,
        programPinned: Boolean = true,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = false,
    ): Flow<List<WorkoutNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutNote(workoutNote: WorkoutNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWorkoutNote(workoutNote: WorkoutNote)

    @Delete
    suspend fun deleteWorkoutNote(workoutNote: WorkoutNote)
}

@Dao
interface MicrocycleNotesDao {
    @Query("SELECT * FROM microcycleNotes WHERE microcycleID = :microcycleID ORDER BY id ASC")
    fun getMicrocycleNotes(microcycleID: Long): Flow<List<MicrocycleNote>>

    @Query(
        "SELECT * FROM microcycleNotes " +
                "WHERE microcycleID = :microcycleID AND pinned = :pinned " +
                "AND programPinned = :programPinned AND mesocyclePinned = :mesocyclePinned " +
                "ORDER BY id ASC"
    )
    fun getPinnedMicrocycleNotes(
        microcycleID: Long,
        pinned: Boolean = true,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
    ): Flow<List<MicrocycleNote>>

    @Query(
        "SELECT * FROM microcycleNotes " +
                "WHERE microcycleID = :microcycleID AND mesocycleID=:mesocycleID " +
                "AND programPinned = :programPinned AND mesocyclePinned=:mesocyclePinned " +
                "ORDER BY id ASC"
    )
    fun getMicrocycleNotesforMesocycle(
        microcycleID: Long,
        mesocycleID: Long,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = true,
    ): Flow<List<MicrocycleNote>>

    @Query(
        "SELECT * FROM microcycleNotes " +
                "WHERE microcycleID = :microcycleID AND programID=:programID " +
                "AND programPinned = :programPinned AND mesocyclePinned=:mesocyclePinned " +
                "ORDER BY id ASC"
    )
    fun getMicrocycleNotesforProgram(
        microcycleID: Long,
        programID: Long,
        programPinned: Boolean = true,
        mesocyclePinned: Boolean = false,
    ): Flow<List<MicrocycleNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicrocycleNote(microcycleNote: MicrocycleNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMicrocycleNote(microcycleNote: MicrocycleNote)

    @Delete
    suspend fun deleteMicrocycleNote(microcycleNote: MicrocycleNote)
}


@Dao
interface MesocycleNotesDao {
    @Query("SELECT * FROM mesocycleNotes WHERE mesocycleID = :mesocycleID ORDER BY id ASC")
    fun getMesocycleNotes(mesocycleID: Long): Flow<List<MesocycleNote>>

    @Query(
        "SELECT * FROM mesocycleNotes " +
                "WHERE mesocycleID = :mesocycleID AND pinned = :pinned " +
                "AND programPinned = :programPinned ORDER BY id ASC"
    )
    fun getPinnedMesocycleNotes(
        mesocycleID: Long,
        pinned: Boolean = true,
        programPinned: Boolean = false,
    ): Flow<List<MesocycleNote>>

    @Query(
        "SELECT * FROM mesocycleNotes " +
                "WHERE mesocycleID = :mesocycleID AND programID=:programID " +
                "AND programPinned = :programPinned " +
                "ORDER BY id ASC"
    )
    fun getMesocycleNotesforProgram(
        mesocycleID: Long,
        programID: Long,
        programPinned: Boolean = true,
    ): Flow<List<MesocycleNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMesocycleNote(mesocycleNote: MesocycleNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMesocycleNote(mesocycleNote: MesocycleNote)

    @Delete
    suspend fun deleteMesocycleNote(mesocycleNote: MesocycleNote)
}

@Dao
interface ProgramNotesDao {
    @Query("SELECT * FROM programNotes WHERE programID = :programID ORDER BY id ASC")
    fun getProgramNotes(programID: Long): Flow<List<ProgramNote>>

    @Query(
        "SELECT * FROM programNotes WHERE programID = :programID AND pinned = :pinned " +
                "ORDER BY id ASC"
    )
    fun getPinnedProgramNotes(
        programID: Long,
        pinned: Boolean = true,
        programPinned: Boolean = false,
        mesocyclePinned: Boolean = false,
        microcyclePinned: Boolean = false,
    ): Flow<List<ProgramNote>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramNote(programNote: ProgramNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProgramNote(programNote: ProgramNote)

    @Delete
    suspend fun deleteProgramNote(programNote: ProgramNote)
}
