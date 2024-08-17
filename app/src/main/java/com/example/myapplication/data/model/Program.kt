@file:UseSerializers(
  MutableRealmIntKSerializer::class,
  RealmAnyKSerializer::class,
  RealmDictionaryKSerializer::class,
  RealmInstantKSerializer::class,
  RealmListKSerializer::class,
  RealmSetKSerializer::class,
  RealmUUIDKSerializer::class,
)

package com.example.myapplication.data.model

import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId

// TODO Add a woy to differentiate between templates and completed/current/active
// TODO Create a data class to track progress in a program
//  i.e. the workouts, microcycles, and mesocycles/blocks completed
// TODO Add Image URI property to Program
@Serializable
class Program() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var name: String? = null
  var author: String? = null
  var dateCreated: RealmInstant = RealmInstant.now()
  var startDate: RealmInstant? = null
  var endDate: RealmInstant? = null
  var shortDescription: String? = null
  var description: String? = null
  var uri: String? = null
  var level: String? = null
  var programEquipment: String? = null
  var daysPerWeek: String? = null
  var programLength: String? = null
  var active: Boolean = false
  var notes: RealmList<ProgramNote> = realmListOf()
  var mesocycles: RealmList<Mesocycle> = realmListOf()
  var owner_id: String = "PUBLIC"
}

@Serializable
data class Programs(val programs: List<Program>)

@Serializable
data class Mesocycles(val mesocycles: List<Mesocycle>)

@Serializable
data class Microcycles(val microcycles: List<Microcycle>)

@Serializable
class Mesocycle() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var name: String? = null
  var description: String? = null
  var dateCreated: RealmInstant = RealmInstant.now()
  var startDate: RealmInstant? = null
  var endDate: RealmInstant? = null
  var order: Int = 0

  //    var tags: String // TODO Should be a List<String>
  var programID: ObjectId? = null
  var microcycles: RealmList<Microcycle> = realmListOf()
  var owner_id: String = "PUBLIC"
}

@Serializable
class Microcycle() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var name: String? = null
  var description: String? = null
  var dateCreated: RealmInstant = RealmInstant.now()
  var startDate: RealmInstant? = null
  var endDate: RealmInstant? = null
  var order: Int = 0
  //    var tags: String // TODO Should be a List<String>
  var programID: ObjectId? = null
  var mesocycleID: ObjectId? = null
  var workouts: RealmList<Workout> = realmListOf()
  var owner_id: String = "PUBLIC"
}
