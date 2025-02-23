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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId

@Serializable
data class User(
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var name: String,
  var username: String,
  var email: String,
  var password: String,
  var defaultWeightUnit: String = MassUnit.POUNDS.abbreviation, // TODO have enum class??????
  var intensity: String = Intensity.RIR.abbreviation // TODO have enum class??????
)