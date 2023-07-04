package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Entity(tableName = "exercises")
data class ExerciseModel(
    @PrimaryKey(autoGenerate = true) val exerciseID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "Exercise Name", // TODO Remove Assignment

//    val exerciseCategory: String? = null, // TODO Remove Assignment // TODO This is too vague, make more specific or remove
//    val exerciseForce: ExerciseForce, // TODO determine if it should be included

    // TODO Make into a List<String> for Multiple Body Parts
//    val muscleGroup: String = "Muscle Group", // TODO MuscleGroup

    // TODO Move to a seperate table/data class. ExerciseProgression Tie to Program, Mesocycle, Microcycle
//    var exerciseProgression: String? = null,

    val exrxExerciseID: String? = null,
    val equipmentID: String, // Equipment
    val muscleGroupID: String,
    val force: String?,
    val mechanicsID: String?,
    val functionID: Int? = null,
    val intensityID: Int? = null,

//    TODO To remove & refactor
    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
//
    // TODO deprecated, replaced by equipmentID. TODO Could be :BarType
//    var barType: String? = null,

    var weightUnit: String? = null, // TODO Could be ENUM -> Default, KG, LBS

    var workingSetRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
    var warmUpSetRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
    var workingSetRestTimerEnabled: Boolean = true,
    var warmUpSetRestTimerEnabled: Boolean = false,

    var videoUri: String? = null,
)
