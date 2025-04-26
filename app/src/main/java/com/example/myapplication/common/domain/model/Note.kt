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

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.ObjectIdSerializer
import com.example.myapplication.common.util.UuidSerializer
import com.example.myapplication.common.util.fakeULID
import com.example.myapplication.common.util.newLazyListItemID
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
data class ExerciseNoteDao(val item: ExerciseNote)

@Serializable
class ExerciseNote() {
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class)
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newLazyListItemID()
  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var dateCreated: Instant = Clock.System.now()
  @Serializable(ObjectIdSerializer::class)
  var exercise: ObjectId? = null
  @Serializable(ObjectIdSerializer::class)
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

@Serializable
data class SupersetNoteDao(val item: SupersetNote)

@Serializable
class SupersetNote() {
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class)
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newLazyListItemID()
  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var dateCreated: Instant = Clock.System.now()

  @Serializable(ObjectIdSerializer::class)
  var exercise: ObjectId? = null

  @Serializable(ObjectIdSerializer::class)
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

@Serializable
data class WorkoutNoteDao(val item: WorkoutNote)

// TODO Are the below necessary? TBD
@Serializable
class WorkoutNote() {
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class)
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newLazyListItemID()

  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false

  @Serializable(ObjectIdSerializer::class)
  var workout: ObjectId? = null

  @Serializable(UuidSerializer::class)
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
data class ProgramNoteDao(val item: ProgramNote)

@Serializable
class ProgramNote() {
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class)
  var id: ObjectId = ObjectId()
  var lazyListID: Long = newLazyListItemID()

  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false

  @Serializable(ObjectIdSerializer::class)
  val program: ObjectId? = null // TODO
  var ownerID: String? = null
  var isPublic: Boolean = false
}

@Serializable
data class MesocycleNoteDao(val item: MesocycleNote)

@Serializable
class MesocycleNote() {
  @SerialName("id")
  @Contextual
  var id: Long = fakeULID()
  var lazyListID: Long = newLazyListItemID()

  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random()
  var note: String? = null
  var pinned: Boolean = false
  var programPinned: Boolean = false
  var mesocycle: Mesocycle? = null
  var ownerID: String? = null
  var isPublic: Boolean = false
}

@Serializable
data class MicrocycleNoteDao(val item: MicrocycleNote)

@Serializable
class MicrocycleNote() {
  @SerialName("id")
  @Contextual
  var id: Long = fakeULID()
  var lazyListID: Long = newLazyListItemID()

  @Serializable(UuidSerializer::class)
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
