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
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO Add a woy to differentiate between templates and completed/current/active
// TODO Create a data class to track progress in a program
//  i.e. the workouts, microcycles, and mesocycles/blocks completed
// TODO Add Image URI property to Program
@Serializable
data class Program(
  @SerialName("id")
  @Serializable(with = ObjectIdSerializer::class)
  var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),

  @Serializable(UuidSerializer::class)
  var uuid: Uuid = Uuid.random(),
  var name: String? = null,
  var author: String? = null,
  var dateCreated: Instant = Clock.System.now(),
  var startDate: Instant? = null,
  var endDate: Instant? = null,
  var shortDescription: String? = null,
  var description: String? = null,
  var uri: String? = null,
  var imageUri: String? = null,
  var imageFont: String? = null,
  var level: String? = null,
  var programEquipment: String? = null,
  var daysPerWeek: String? = null,
  var programLength: String? = null,
  var isActive: Boolean = false,
  var isCompleted: Boolean = false,
  var notes: List<ProgramNote> = emptyList(),
  var mesocycles: List<Mesocycle> = emptyList(),
  var ownerID: String = "PUBLIC",
  var isPublic: Boolean = false,
  private val type: String = "Program",
)

@Serializable
data class ProgramDao(val Program: Program)

@Serializable
data class Programs(val programs: List<Program>)

@Serializable
data class Mesocycles(val mesocycles: List<Mesocycle>)

@Serializable
data class Microcycles(val microcycles: List<Microcycle>)

@Serializable
data class MesocycleDao(val Mesocycle: Mesocycle)

@Serializable
data class Mesocycle(
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var name: String? = null,
  var description: String? = null,
  var dateCreated: Instant = Clock.System.now(),
  var startDate: Instant? = null,
  var endDate: Instant? = null,
  var order: Int = 0,
  //    var tags: String // TODO Should be a List<String>,
  @Serializable(with = ObjectIdSerializer::class) var programID: ObjectId? = null,
  var microcycles: List<Microcycle> = emptyList(),
  var ownerID: String = "PUBLIC",
  var isPublic: Boolean = false,
  private val type: String = "Mesocycle",
)

@Serializable
data class MicrocycleDao(val Microcycle: Microcycle)

@Serializable
data class Microcycle(
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var name: String? = null,
  var description: String? = null,
  var dateCreated: Instant = Clock.System.now(),
  var startDate: Instant? = null,
  var endDate: Instant? = null,
  var order: Int = 0,
  //    var tags: String // TODO Should be a List<String>,
  @Serializable(with = ObjectIdSerializer::class) var programID: ObjectId? = null,
  @Serializable(with = ObjectIdSerializer::class) var mesocycleID: ObjectId? = null,
  var workouts: List<Workout> = emptyList(),
  var ownerID: String = "PUBLIC",
  var isPublic: Boolean = false,
  private val type: String = "Microcycle",
)


