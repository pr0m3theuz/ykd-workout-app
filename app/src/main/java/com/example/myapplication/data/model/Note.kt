@file:UseSerializers(
//  MutableRealmIntKSerializer::class,
//  RealmAnyKSerializer::class,
//  RealmDictionaryKSerializer::class,
//  RealmInstantKSerializer::class,
//  RealmListKSerializer::class,
//  RealmSetKSerializer::class,
//  RealmUUIDKSerializer::class,
)
@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.model

import com.example.myapplication.utils.fakeULID
import com.example.myapplication.utils.newID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO Switch to use UUIDs instead of IDs
@Serializable
class ExerciseNote() {
  @SerialName("id")
  @Contextual
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newID()
  @Contextual
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var dateCreated: Instant = Clock.System.now()
  @Contextual
  var exercise: ObjectId? = null
  @Contextual
  var workout: ObjectId? = null

  //    var program: Program
  //    var programPinned: Boolean = false
  //    var mesocycle: Mesocycle
  //    var mesocyclePinned: Boolean = false
  //    var microcycle: Microcycle
  //    var microcyclePinned: Boolean = false
  var ownerID: String? = null
  var isPublic: Boolean = false
}

data class WorkoutNoteDao(val item: WorkoutNote)

// TODO Are the below necessary? TBD
@Serializable
class WorkoutNote() {
  @SerialName("id")
  @Contextual
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newID()
  @Contextual
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  @Contextual
  var workout: ObjectId? = null
  @Contextual
  var workoutUUID: Uuid? = null
  //    var program: Program
  //    var programPinned: Boolean = false
  //    var mesocycle: Mesocycle
  //    var mesocyclePinned: Boolean = false
  //    var microcycle: Microcycle
  //    var microcyclePinned: Boolean = false
  var ownerID: String? = null
  var isPublic: Boolean = false
}

@Serializable
class ProgramNote() {
  @SerialName("id")
  @Contextual
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newID()
  @Contextual
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  @Contextual
  val program: ObjectId? = null // TODO
  var ownerID: String? = null
  var isPublic: Boolean = false
}

@Serializable
class MesocycleNote() {
  @SerialName("id")
  @Contextual
  var id: Long = fakeULID()
  var lazyListID: Long = newID()
  @Contextual
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var programPinned: Boolean = false
  var mesocycle: Mesocycle? = null
  var ownerID: String? = null
  var isPublic: Boolean = false
}

class MicrocycleNote() {
  @SerialName("id")
  @Contextual
  var id: Long = fakeULID()
  var lazyListID: Long = newID()
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var microcycle: Microcycle? = null

  // var programID: Long? = null
  var programPinned: Boolean = false

  // var mesocycleID: Long? = null
  var mesocyclePinned: Boolean = false
  var ownerID: String? = null
  var isPublic: Boolean = false
}
