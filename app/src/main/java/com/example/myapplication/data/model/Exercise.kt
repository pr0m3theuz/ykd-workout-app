@file:UseSerializers(
//  MutableRealmIntKSerializer::class,
//  RealmAnyKSerializer::class,
//  RealmDictionaryKSerializer::class,
//  RealmInstantKSerializer::class,
//  RealmListKSerializer::class,
//  RealmSetKSerializer::class,
//  RealmUUIDKSerializer::class
)

package com.example.myapplication.data.model

import androidx.annotation.Keep
import com.example.myapplication.utils.newID
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Exercises(val exercises: List<Exercise>)

@Serializable
data class ExerciseDao(var item: Exercise)

@Keep
@Serializable
data class Exercise(
  @SerialName("_id")
  @Contextual var _id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newID(),
  @OptIn(ExperimentalUuidApi::class)
  @Contextual var uuid: Uuid = Uuid.random(),
  var name: String,

  //    val exerciseCategory: String? = null // TODO Remove Assignment // TODO This is too vague
  // make more specific or remove
  //    val exerciseForce: ExerciseForce // TODO determine if it should be included

  // TODO Make into a List<String> for Multiple Body Parts
  //    val muscleGroup: String = "Muscle Group" // TODO MuscleGroup

  // TODO Move to a seperate table/data class. ExerciseProgression Tie to Program Mesocycle
  // Microcycle
  //    var exerciseProgression: String? = null

  var exrxExerciseID: String? = null,
  var equipmentID: String? = null, // Equipment
  var primaryMuscleID: String? = null,
  var muscleGroupID: String? = null,
  var utilityID: String? = null, // May be useless
  var mechanicsID: String? = null,
  var force: String? = null,
  var functionID: String? = null,
  var intensityID: String? = null,

  //    TODO To remove & refactor
  //    var notes: String? = null // TODO Create Note Object that takes a string and a boolean for
  // pinned status
  //    var pinnedNotes: String? = null
  //    var notesPinned: Boolean = false
  //
  // TODO deprecated replaced by equipmentID. TODO Could be :BarType
  //    var barType: String? = null

  var weightUnit: String? = null, // TODO Could be ENUM -> Default KG LBS
  var workingSetRestInWholeMilliseconds: Long =
    3.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
  var warmUpSetRestInWholeMilliseconds: Long =
    3.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
  var workingSetRestTimerEnabled: Boolean = true,
  var warmUpSetRestTimerEnabled: Boolean = false,

//  @Serializable(URLSerializer::class)
  var mediaURI: String? = null,

    //  @Serializable(URLSerializer::class)
  var pageURI: String? = null,
  var ownerID: String = "PUBLIC",
  var isPublic: Boolean = true,
) {
  fun toJson(): String {
    return Json.encodeToString(this)
  }
}


/*
object URLSerializer : KSerializer<String> {
  override val descriptor: SerialDescriptor = String.serializer().descriptor

  @OptIn(ExperimentalStdlibApi::class)
  override fun deserialize(decoder: Decoder): String {
    return decoder.decodeString().hexToByteArray().toString(Charsets.UTF_8)
  }

  @OptIn(ExperimentalStdlibApi::class)
  override fun serialize(encoder: Encoder, value: String) {
    encoder.encodeString(value.encodeToByteArray().toHexString())
  }
}*/
