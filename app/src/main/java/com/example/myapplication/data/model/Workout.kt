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

import android.os.Parcel
import android.os.Parcelable
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
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
class Workout() : RealmObject {
  @PrimaryKey var _id: ObjectId = ObjectId()
  var lazyListItemID: Long = newID()
  var uuid: RealmUUID = RealmUUID.random()
  var name: String? = null // TODO make name default = User
  var workoutAuthor: String? = null
  var notes: RealmList<WorkoutNote> = realmListOf()
  var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds
  var workoutType: String? = null
  var dateCreated: RealmInstant = RealmInstant.now()
  var workoutStarted: RealmInstant? = null
  var workoutEnded: RealmInstant? = null
  var day: Int? = null
  var exercisesSets: RealmList<ExerciseSets> = realmListOf()
  var totalVolumeInPounds: Double? = 0.0
  var totalVolumeInKilograms: Double? = 0.0
  var programID: ObjectId? = null

  //    val mesocycleID: Long? = null
  var microcycleID: ObjectId? = null
  var ownerID: String = "PUBLIC"
}

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

@Serializable data class Workouts(val workouts: List<Workout>)

fun Workout.formatWorkoutDuration(): String {

  return workoutDuration.toDuration(DurationUnit.MILLISECONDS).toComponents {
    hours,
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
  exercisesSets.forEach { exerciseSets -> totalSets += exerciseSets.sets.size }
  return totalSets
}

fun Workout.calculateTotalVolume(userDefaultWeightUnit: String): Double? {
  exercisesSets.forEach { exerciseSets ->
    totalVolumeInPounds =
      totalVolumeInPounds?.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
        ?: 0.0.plus(exerciseSets.totalVolumeInPounds ?: 0.0)
    totalVolumeInKilograms =
      totalVolumeInKilograms?.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
        ?: 0.0.plus(exerciseSets.totalVolumeInKilograms ?: 0.0)
  }
  return when (userDefaultWeightUnit) {
    MassUnit.POUNDS.abbreviation -> totalVolumeInPounds
    MassUnit.KILOGRAMS.abbreviation -> totalVolumeInKilograms
    else -> {
      totalVolumeInPounds
    }
  }
}
