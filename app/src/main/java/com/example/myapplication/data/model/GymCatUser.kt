@file:UseSerializers(
  //  MutableRealmIntKSerializer::class,
  //  RealmAnyKSerializer::class,
  //  RealmDictionaryKSerializer::class,
  //  RealmInstantKSerializer::class,
  //  RealmListKSerializer::class,
  //  RealmSetKSerializer::class,
  //  RealmUUIDKSerializer::class,
)

package com.example.myapplication.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId

@Serializable
data class User(
  @SerialName("_id") @Contextual var _id: ObjectId = ObjectId(),
  var name: String,
  var username: String,
  var email: String,
  var password: String,
  var defaultWeightUnit: String = MassUnit.POUNDS.abbreviation, // TODO have enum class??????
  var intensity: String = Intensity.RIR.abbreviation // TODO have enum class??????
)