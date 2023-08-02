package com.example.myapplication.data

import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class Exercise(
  @PrimaryKey val exerciseID: Long = fakeULID(),
  val lazyListItemID: Long = newID(),
  val uuid: RealmUUID = RealmUUID.random(),
  var name: String = "Exercise Name", // TODO Remove Assignment

  //    val exerciseCategory: String? = null, // TODO Remove Assignment // TODO This is too vague,
  // make more specific or remove
  //    val exerciseForce: ExerciseForce, // TODO determine if it should be included

  // TODO Make into a List<String> for Multiple Body Parts
  //    val muscleGroup: String = "Muscle Group", // TODO MuscleGroup

  // TODO Move to a seperate table/data class. ExerciseProgression Tie to Program, Mesocycle,
  // Microcycle
  //    var exerciseProgression: String? = null,

  val exrxExerciseID: String? = null,
  var equipmentID: String, // Equipment
  var muscleGroupID: String,
  var utilityID: String? = null, // May be useless
  var mechanicsID: String?,
  var force: String?,
  var functionID: String? = null,
  var intensityID: String? = null,

  //    TODO To remove & refactor
  //    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for
  // pinned status
  //    var pinnedNotes: String? = null,
  //    var notesPinned: Boolean = false,
  //
  // TODO deprecated, replaced by equipmentID. TODO Could be :BarType
  //    var barType: String? = null,

  var weightUnit: String? = null, // TODO Could be ENUM -> Default, KG, LBS
  var workingSetRestInWholeMilliseconds: Long =
    2.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
  var warmUpSetRestInWholeMilliseconds: Long =
    2.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
  var workingSetRestTimerEnabled: Boolean = true,
  var warmUpSetRestTimerEnabled: Boolean = false,
  var mediaURI: String? = null,
  var pageURI: String? = null,
  var primaryMuscleID: String? = null,
) : RealmObject {}
