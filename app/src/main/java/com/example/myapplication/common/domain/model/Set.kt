@file:UseSerializers(
  /*  MutableRealmIntKSerializer::class,
  RealmAnyKSerializer::class,
  RealmDictionaryKSerializer::class,
  RealmInstantKSerializer::class,
  RealmListKSerializer::class,
  RealmSetKSerializer::class,
  RealmUUIDKSerializer::class,*/
  InstantSerializer::class,
  UuidSerializer::class,
  ObjectIdSerializer::class,
)
@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.InstantSerializer
import com.example.myapplication.common.util.ObjectIdSerializer
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.ProgressionTypeSerializer
import com.example.myapplication.common.util.SetTypeSerializer
import com.example.myapplication.common.util.UuidSerializer
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class SetDao(var Set: Set)

@Serializable
data class CompletedSetsDao(var CompletedSets: Set)

@Serializable
data class Set(
  @SerialName("id") var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var setNumber: Int? = 1,
  var weightPounds: Double? = null,
  var weightKilograms: Double? = null,
  var repetitions: Int? = null,
  var ratePerceivedEffort: Double? = null,
  var repetitionsInReserve: Double? = null,
  var targetWeightPounds: Double? = null,
  var targetWeightKilograms: Double? = null,
  var incrementWeightPounds: Double? = null,
  var incrementWeightKilograms: Double? = null,
  var targetRepetitions: Int? = null,
  var targetMinimumRepetitions: Int? = null, // for progression scheme & utils
  var targetMaximumRepetitions: Int? = null, // for progression scheme & utils
  var targetRatePerceivedEffort: Double? = null,
  var targetRepetitionsInReserve: Double? = null,
  var percentage1RM: Double? = null,
  var estimated1RM: Double? = null, // TODO implement functions to update
  var targetPercentage1RM: Double? = null, // for progression scheme & utils
  var setCompleted: Boolean = false,
  var setVolumeInPounds: Double? = null,
  var setVolumeInKilograms: Double? = null,
  var workoutID: ObjectId,
  var exerciseID: ObjectId? = null,
  var exerciseSetsID: ObjectId,
  var microcycleID: ObjectId? = null,
  var mesocycleID: ObjectId? = null,
  var programID: ObjectId? = null,
  @Serializable(SetTypeSerializer::class) var setTypeID: SetType = SetType.normalSet,
  @Serializable(ProgressionTypeSerializer::class) var progressionSchemeID: ProgressionType? = null,
  //    To use in query for previous sets
  var dateCreated: Instant? = Clock.System.now(),
  //    var programUUID: Uuid? = null
  //    var mesocycleUUID: Uuid? = null
  //    var microcycleUUID: Uuid? = null
  // TODO Copies Strong App - Allows different rest intervals between sets
  //  var setRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
  //  var setRestTimerEnabled: Boolean = false,
  var hashedSetSignature: String? = null,
  var hashedWorkoutSignature: String? = null,
  var ownerID: String,
  var isPublic: Boolean = false,
  private val type: String = "Set",
)

fun Set.setVolume(weightUnit: String): Double {
  return if (
    this.weightPounds != null &&
    this.repetitions != null &&
    weightUnit == MassUnit.POUNDS.abbreviation
  ) {
    this.setVolumeInPounds = this.weightPounds!!.times(this.repetitions!!)
    weightPounds!!.times(this.repetitions!!)
  } else if (
    this.weightKilograms != null &&
    this.repetitions != null &&
    weightUnit == MassUnit.KILOGRAMS.abbreviation
  ) {
    this.setVolumeInKilograms = this.weightKilograms!!.times(this.repetitions!!)
    this.weightKilograms!!.times(this.repetitions!!)
  } else {
    0.0
  }
}

fun Set.setSummaryRIR(weightUnit: String): String {
  // TODO get user default weight unit or exercise specific weight unit

  return "${
    if (MassUnit.POUNDS.abbreviation == weightUnit) {
      doubleFormatted(this.weightPounds)
    } else {
      doubleFormatted(this.weightKilograms)
    }
  } $weightUnit × ${this.repetitions} reps${
    if (this.repetitionsInReserve != null)
      " @ ${doubleFormatted(this.repetitionsInReserve)} RIR"
    else ""
  }"
}

//  ${doubleFormatted(weightKilograms)} × $repetitions ${
//    if (repetitionsInReserve != null) "@ ${
//      doubleFormatted(
//        repetitionsInReserve
//      )
//    } RIR" else ""
//  }"

fun Set.previousSetSummaryRIR(weightUnit: String): String {
  // TODO get user default weight unit or exercise specific weight unit
  return "${doubleFormatted(this.weightPounds)} × ${this.repetitions} ${
    if (this.repetitionsInReserve != null)
      " @ ${doubleFormatted(this.repetitionsInReserve)} RIR"
    else ""
  }"
}

fun Set.setTargetSummaryRIR(weightUnit: String): String {
  // TODO get user default weight unit or exercise specific weight unit
  return "${doubleFormatted(this.targetWeightPounds).ifBlank { "–" }} $weightUnit"
    .plus(" × ")
    .plus("${this.targetRepetitions ?: "–"} reps")
    .plus(
      if (this.targetRepetitionsInReserve != null)
        " @ ${doubleFormatted(this.targetRepetitionsInReserve)} RIR"
      else ""
    )
}

fun Set.setSummaryRPE(): String {
  return "${doubleFormatted(this.weightPounds).ifBlank { "–" }} × ${this.repetitions ?: "–"} reps @ ${
    doubleFormatted(
      this.ratePerceivedEffort
    )
  } RPE"
  //  "${doubleFormatted(weightKilograms)} × $repetitions @ ${doubleFormatted(ratePerceivedEffort)}
  // RPE"
}

fun Set.convertPoundToKg(): Double {
  return this.weightPounds?.times(0.45359237) ?: 0.0
}

fun Set.convertKgToPound(): Double {
  return this.weightKilograms?.times(2.2046226218) ?: 0.0
}

fun Set.getWeight(weightUnit: String?) {
  // TODO Check if this would work or would have to use viewModel
  // TODO take exercise.weightUnit to update weight
  //  Uuid.random().toString()
}

@Serializable
data class Sets(val sets: List<Set>)

fun Double.convertPoundToKg(): Double {
  return this.times(0.45359237)
}

fun Double.convertKgToPound(): Double {
  return this.times(2.2046226218)
}
