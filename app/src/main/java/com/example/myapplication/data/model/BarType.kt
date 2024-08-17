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

import com.example.myapplication.utils.newID
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import kotlinx.serialization.UseSerializers

// TODO Create screen to edit/create bar type
// TODO Could be useful for Plate Calculator Feature
// @Entity(tableName = "barType")
data class BarType(
    val id: Long = newID(),
    val name: String,
    var barWeightPounds: Double,
    var barWeightKilograms: Double,
    var owner_id: String? = null
)

data class WeightUnit(
    val id: Long = newID(),
    val name: String,
    val abbreviation: String,
)

enum class MassUnit(val abbreviation: String) {
    POUNDS("lbs"),
    KILOGRAMS("kg"),
}

enum class Intensity(val abbreviation: String) {
    RIR("RIR"),
    RPE("RPE"),
}