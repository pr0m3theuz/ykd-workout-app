
@file:UseSerializers(
  /*  MutableRealmIntKSerializer::class,
    RealmAnyKSerializer::class,
    RealmDictionaryKSerializer::class,
    RealmInstantKSerializer::class,
    RealmListKSerializer::class,
    RealmSetKSerializer::class,
    RealmUUIDKSerializer::class,*/
)
@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.ObjectIdSerializer
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.UuidSerializer
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object ProgressionTypeSerializer : KSerializer<ProgressionType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("com.example.MyApplication.ProgressionType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): ProgressionType {
    return ProgressionType.entries[(decoder.decodeInt())]
  }

  override fun serialize(encoder: Encoder, value: ProgressionType) {
    encoder.encodeInt(value.ordinal)
  }
}

object SetTypeSerializer : KSerializer<SetType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("com.example.MyApplication.ProgressionType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): SetType {
    return SetType.getSetType(decoder.decodeInt())
  }

  override fun serialize(encoder: Encoder, value: SetType) {
    encoder.encodeInt(value.setTypeID)
  }
}

@Serializable
data class SetDao(var Set: Set)

@Serializable
data class Set(
  @SerialName("id") @Serializable(ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var setNumber: Int? = 0,
  var weightPounds: Double? = null,
  var weightKilograms: Double? = null,
  var repetitions: Int? = null,
  var ratePerceivedEffort: Double? = null,
  var repetitionsInReserve: Double? = null,
  // TODO add targetWeight Mass Unit
  var targetWeightPounds: Double? = null,
  var targetWeightKilograms: Double? = null,
  var incrementWeightPounds: Double? = null,
  var incrementWeightKilograms: Double? = null,
  var targetRepetitions: String? = null,
  // TODO Update Realm Schema to include targetMinimumRepetitions and targetMaximumRepetitions
  var targetMinimumRepetitions: Int? = null, // TODO for progression scheme & utils
  var targetMaximumRepetitions: Int? = null, // TODO for progression scheme & utils
  var targetRatePerceivedEffort: Double? = null,
  var targetRepetitionsInReserve: Double? = null,
  var percentage1RM: Double? = 1.0,
  // TODO Update Realm Schema to include targetPercentage1RM
  var targetPercentage1RM: Double? = null, // TODO for progression scheme & utils
  var setCompleted: Boolean = false,
  @Serializable(ObjectIdSerializer::class) var workoutID: ObjectId,
  @Serializable(ObjectIdSerializer::class) var exerciseID: ObjectId? = null,
  @Serializable(ObjectIdSerializer::class) var exerciseSetsID: ObjectId,
  //    var exerciseOrder: Int = 1
  @Serializable(SetTypeSerializer::class) var setTypeID: SetType = SetType.normalSet,
  //  @Serializable(ObjectIdSerializer::class) val exerciseSet: ObjectId, // TODO Use backlinks for ExerciseSets or not
  @Serializable(ProgressionTypeSerializer::class) var progressionSchemeID: ProgressionType? = null, // TODO Create ProgressionScheme class/enum and RealmObject
  //    To use in query for previous sets
  var dateCreated: Instant? = Clock.System.now(),
  //    var programUUID: Uuid? = null
  //    var mesocycleUUID: Uuid? = null
  //    var microcycleUUID: Uuid? = null
  // TODO Copies Strong App - Allows different rest intervals between sets
//  var setRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
//  var setRestTimerEnabled: Boolean = false,
  var ownerID: String,
  var isPublic: Boolean = false
)

fun Set.setVolume(weightUnit: String): Double {
  return if (
    weightPounds != null && repetitions != null && weightUnit == MassUnit.POUNDS.abbreviation
  ) {
    weightPounds!!.times(repetitions!!)
  } else if (
    weightKilograms != null && repetitions != null && weightUnit == MassUnit.KILOGRAMS.abbreviation
  ) {
    weightKilograms!!.times(repetitions!!)
  } else {
    0.0
  }
}

fun Set.setSummaryRIR(weightUnit: String): String {

  return "${doubleFormatted(weightPounds)} $weightUnit × $repetitions ${
    if (repetitionsInReserve != null) "@ ${
      doubleFormatted(
        repetitionsInReserve
      )
    }" else ""
  }"

  //  ${doubleFormatted(weightKilograms)} × $repetitions ${
  //    if (repetitionsInReserve != null) "@ ${
  //      doubleFormatted(
  //        repetitionsInReserve
  //      )
  //    } RIR" else ""
  //  }"
}

fun Set.setSummaryRPE(): String {
  return "${doubleFormatted(weightPounds)} × $repetitions @ ${doubleFormatted(ratePerceivedEffort)} RPE"
  //  "${doubleFormatted(weightKilograms)} × $repetitions @ ${doubleFormatted(ratePerceivedEffort)}
  // RPE"
}

fun Set.convertPoundToKg(): Double {
  return weightPounds?.times(0.45359237) ?: 0.0
}

fun Set.convertKgToPound(): Double {
  return weightKilograms?.times(2.2046226218) ?: 0.0
}

fun Set.getWeight(weightUnit: String?) {
  // TODO Check if this would work or would have to use viewModel
  // TODO take exercise.weightUnit to update weight
  //  Uuid.random().toString()
}

@Serializable
data class Sets(val sets: List<Set>)
