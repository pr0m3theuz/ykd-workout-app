@file:UseSerializers(
  MutableRealmIntKSerializer::class,
  RealmAnyKSerializer::class,
  RealmDictionaryKSerializer::class,
  RealmInstantKSerializer::class,
  RealmListKSerializer::class,
  RealmSetKSerializer::class,
  RealmUUIDKSerializer::class
)

package com.example.myapplication.data.model

import com.example.myapplication.newID
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
class Exercise() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var name: String = "Exercise Name" // TODO Remove Assignment

  //    val exerciseCategory: String? = null // TODO Remove Assignment // TODO This is too vague
  // make more specific or remove
  //    val exerciseForce: ExerciseForce // TODO determine if it should be included

  // TODO Make into a List<String> for Multiple Body Parts
  //    val muscleGroup: String = "Muscle Group" // TODO MuscleGroup

  // TODO Move to a seperate table/data class. ExerciseProgression Tie to Program Mesocycle
  // Microcycle
  //    var exerciseProgression: String? = null

  var exrxExerciseID: String? = null
  var equipmentID: String? = null // Equipment
  var primaryMuscleID: String? = null
  var muscleGroupID: String? = null
  var utilityID: String? = null // May be useless
  var mechanicsID: String? = null
  var force: String? = null
  var functionID: String? = null
  var intensityID: String? = null

  //    TODO To remove & refactor
  //    var notes: String? = null // TODO Create Note Object that takes a string and a boolean for
  // pinned status
  //    var pinnedNotes: String? = null
  //    var notesPinned: Boolean = false
  //
  // TODO deprecated replaced by equipmentID. TODO Could be :BarType
  //    var barType: String? = null

  var weightUnit: String? = null // TODO Could be ENUM -> Default KG LBS
  var workingSetRestInWholeMilliseconds: Long =
    3.toDuration(DurationUnit.MINUTES).inWholeMilliseconds
  var warmUpSetRestInWholeMilliseconds: Long =
    3.toDuration(DurationUnit.MINUTES).inWholeMilliseconds
  var workingSetRestTimerEnabled: Boolean = true
  var warmUpSetRestTimerEnabled: Boolean = false
  var mediaURI: String? = null
  var pageURI: String? = null
}

@Serializable
data class Exercises(val exercises: List<Exercise>)