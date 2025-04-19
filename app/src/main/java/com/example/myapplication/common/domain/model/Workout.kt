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

import android.os.Parcel
import android.os.Parcelable
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.datetime.Clock
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


object ObjectIdSerializer : KSerializer<ObjectId> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("org.bson.types.ObjectId", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): ObjectId {
    return ObjectId(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: ObjectId) {
    encoder.encodeString(value.toHexString())
  }
}

object UuidSerializer : KSerializer<Uuid> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("app.Uuid", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Uuid {
    return Uuid.parseHex(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: Uuid) {
    encoder.encodeString(value.toHexString())
  }
}

@Serializable
data class WorkoutDao(var Workout: Workout)

@Serializable
data class Workout(
  @SerialName("id")
  @Serializable(ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var lazyListItemID: Long = newLazyListItemID(),
  @Serializable(UuidSerializer::class) var uuid: Uuid = Uuid.random(),
  var name: String, // TODO make name default = User
  var workoutAuthor: String? = null,
  var notes: List<WorkoutNote> = emptyList(),
  var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds,
  var workoutType: String? = null,
  var dateCreated: kotlinx.datetime.Instant = Clock.System.now(), // TODO Decide on whether to use Kotlinx.DateTime or Java.Time
  var workoutStarted: kotlinx.datetime.Instant? = null, // TODO Decide on whether to use Kotlinx.DateTime or Java.Time
  var workoutEnded: kotlinx.datetime.Instant? = null, // TODO Decide on whether to use Kotlinx.DateTime or Java.Time
  var day: Int? = null,
  @Serializable(with = WorkoutItemListSerializer::class) var workoutItems: List<WorkoutItem> = emptyList(),
  var totalVolumeInPounds: Double? = 0.0,
  var totalVolumeInKilograms: Double? = 0.0,
  var order: Int? = null, // For sorting workouts in a program
  @Serializable(ObjectIdSerializer::class) var programID: ObjectId? = null,
  @Serializable(ObjectIdSerializer::class) val mesocycleID: ObjectId? = null,
  @Serializable(ObjectIdSerializer::class) var microcycleID: ObjectId? = null,
  var ownerID: String = "",
  var isPublic: Boolean = false,
//  var type: String? = null,
)

@Parcelize
class WorkoutParcel(var workout: String) : Parcelable {
  private companion object : Parceler<WorkoutParcel> {
    override fun create(parcel: Parcel): WorkoutParcel =
      WorkoutParcel(parcel.readString().toString())

    override fun WorkoutParcel.write(parcel: Parcel, flags: Int) {
      parcel.writeString(workout)
    }
  }
}

@Serializable
data class Workouts(val workouts: List<Workout>)

fun Workout.formatWorkoutDuration(): String {
  return workoutDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { hours,
                                                                              minutes,
                                                                              seconds,
                                                                              _ ->
    when (hours) {
      0L -> "$minutes" + "m"
      else -> "$hours" + "h" + " " + minutes.toString().padStart(2, '0') + "m"
    }
  }
}

fun Workout.formatWorkoutDate(): String {
  val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
  //  TODO Confirm if UTC or local
  val offset = ZoneId.systemDefault().rules.getOffset(Instant.now())
  return if (workoutStarted != null) {
    LocalDateTime.ofEpochSecond(
      workoutStarted!!.epochSeconds,
      workoutStarted!!.nanosecondsOfSecond,
      offset,
    )
      .format(dateTimeFormatter)
  } else {
    ""
  }
}

fun Workout.formatLongWorkoutStarted(): String {
  val dayTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")
  val offset = ZoneId.systemDefault().rules.getOffset(Instant.now())
  //  TODO Confirm if UTC or local
  return if (workoutStarted != null) {
    LocalDateTime.ofEpochSecond(
      workoutStarted!!.epochSeconds,
      workoutStarted!!.nanosecondsOfSecond,
      offset,
    )
      .format(dayTimeFormatter)
  } else {
    ""
  }
}

fun Workout.totalSets(): Int {
  var totalSets: Int = 0
  workoutItems.forEach { workoutItem ->
    when (workoutItem) {
      is ExerciseSets -> {
        totalSets += workoutItem.sets.size
      }

      is Superset -> {
        workoutItem.exercisesSets.forEach { exerciseSets ->
          totalSets += exerciseSets.sets.size
        }
      }
    }
  }


  return totalSets
}

fun Workout.calculateTotalVolume(userDefaultWeightUnit: String): Double? {
  workoutItems.forEach { workoutItem ->
    when (workoutItem) {
      is ExerciseSets -> {
        totalVolumeInPounds =
          this.totalVolumeInPounds?.plus(workoutItem.totalVolumeInPounds ?: 0.0)
            ?: 0.0.plus(workoutItem.totalVolumeInPounds ?: 0.0)
        totalVolumeInKilograms =
          totalVolumeInKilograms?.plus(workoutItem.totalVolumeInKilograms ?: 0.0)
            ?: 0.0.plus(workoutItem.totalVolumeInKilograms ?: 0.0)
      }

      is Superset -> {
        workoutItem.exercisesSets.forEach { exerciseSets ->
          totalVolumeInPounds =
            this.totalVolumeInPounds?.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
              ?: 0.0.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
          totalVolumeInKilograms =
            totalVolumeInKilograms?.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
              ?: 0.0.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
        }
      }
    }

  }
  return when (userDefaultWeightUnit) {
    MassUnit.POUNDS.abbreviation -> totalVolumeInPounds
    MassUnit.KILOGRAMS.abbreviation -> totalVolumeInKilograms
    else -> {
      this.totalVolumeInPounds
    }
  }
}

/* Note: Decide on whether to keep or delete
fun Workout.calculateTotalVolumeInPounds(): Double? {
  exercisesSets.forEach { exerciseSets ->
    totalVolumeInPounds =
      this.totalVolumeInPounds?.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
        ?: 0.0.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
  }
  Log.i("Workout.totalVolumeInPounds", "$totalVolumeInPounds")
  return totalVolumeInPounds
}

fun Workout.calculateTotalVolumeInKilograms(): Double? {
  exercisesSets.forEach { exerciseSets ->
    totalVolumeInKilograms =
      totalVolumeInKilograms?.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
        ?: 0.0.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
  }
  Log.i("Workout.totalVolumeInKilograms", "$totalVolumeInKilograms")
  return totalVolumeInKilograms
}*/
