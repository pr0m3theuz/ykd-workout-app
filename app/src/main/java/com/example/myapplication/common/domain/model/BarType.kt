@file:UseSerializers(
//    MutableRealmIntKSerializer::class,
//    RealmAnyKSerializer::class,
//    RealmDictionaryKSerializer::class,
//    RealmInstantKSerializer::class,
//    RealmListKSerializer::class,
//    RealmSetKSerializer::class,
//    RealmUUIDKSerializer::class
)

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.serialization.UseSerializers

// TODO Create screen to edit/create bar type
// TODO Could be useful for Plate Calculator Feature
// @Entity(tableName = "barType")
data class BarType(
  val id: Long = newLazyListItemID(),
  val name: String,
  var barWeightPounds: Double,
  var barWeightKilograms: Double,
  var ownerID: String? = null
)

data class WeightUnit(
  val id: Long = newLazyListItemID(),
  val name: String,
  val abbreviation: String,
)

//enum class MassUnit(val abbreviation: String) {
//    POUNDS("lbs"),
//    KILOGRAMS("kg"),
//}

enum class Intensity(val abbreviation: String) {
    RIR("RIR"),
    RPE("RPE"),
}