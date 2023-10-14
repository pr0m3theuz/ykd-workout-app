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

import com.example.myapplication.utils.doubleFormatted
import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId

@Serializable
class Set() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var setNumber: Int? = 0
  var weightPounds: Double? = null
  var weightKilograms: Double? = null
  var repetitions: Int? = null
  var ratePerceivedEffort: Double? = null
  var repetitionsInReserve: Double? = null
  var targetWeight: Double? = null
  var targetRepetitions: String? = null
  var targetRatePerceivedEffort: Double? = null
  var targetRepetitionsInReserve: Double? = null
  var percentage1RM: Double? = 1.0

  // var targetPercentage1RM: Double? = null
  var setCompleted: Boolean = false

  //    var exerciseOrder: Int = 1
  var setTypeID: Int = 1
  val exerciseSet: RealmResults<ExerciseSets> by
  backlinks(ExerciseSets::sets) // TODO Use backlinks for ExerciseSets or not
  var progressionSchemeID: Int = 0

  //    To use in query for previous sets
  var exerciseID: ObjectId? = null
    var dateCreated: RealmInstant? = RealmInstant.now()
  //    var programUUID: RealmUUID? = null
  //    var mesocycleUUID: RealmUUID? = null
  //    var microcycleUUID: RealmUUID? = null
}

fun Set.setVolume(weightUnit: String): Double {
  return if (weightPounds != null && repetitions != null && weightUnit == MassUnit.POUNDS.abbreviation) {
    weightPounds!!.times(repetitions!!)
  } else if (weightKilograms != null && repetitions != null && weightUnit == MassUnit.KILOGRAMS.abbreviation) {
    weightKilograms!!.times(repetitions!!)
  } else {
    0.0
  }
}

fun Set.setTextRIR(weightUnit: String): String {

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

fun Set.setTextRPE(): String {
  return "${doubleFormatted(weightPounds)} × $repetitions @ ${doubleFormatted(ratePerceivedEffort)} RPE"
//  "${doubleFormatted(weightKilograms)} × $repetitions @ ${doubleFormatted(ratePerceivedEffort)} RPE"
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
}

@Serializable
data class Sets(val sets: List<Set>)
