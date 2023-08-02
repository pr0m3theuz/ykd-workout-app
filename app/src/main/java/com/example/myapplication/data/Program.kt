package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import java.time.LocalDateTime

// TODO Add a woy to differentiate between templates and completed/current/active
// TODO Create a data class to track progress in a program
//  i.e. the workouts, microcycles, and mesocycles/blocks completed


data class Program(
  @PrimaryKey(autoGenerate = true) val programID: Long = fakeULID(),
  val lazyListItemID: Long = newID(),
  val uuid: RealmUUID = RealmUUID.random(),
  var name: String,
  var author: String? = null,
  var dateCreated: LocalDateTime = LocalDateTime.now(),
  var startDate: LocalDateTime? = null,
  var endDate: LocalDateTime? = null,
  var shortDescription: String? = null,
  var description: String? = null,
  var uri: String? = null,
  var level: String? = null,
  var programEquipment: String? = null,
  var daysPerWeek: String? = null,
  var programLength: String? = null,
//    var tags: String, // TODO Should be a List<String>
)

data class Mesocycle(
  @PrimaryKey(autoGenerate = true) val mesocycleID: Long = fakeULID(),
  val lazyListItemID: Long = newID(),
  val uuid: RealmUUID = RealmUUID.random(),
  var name: String,
  var description: String? = null,
  var dateCreated: LocalDateTime = LocalDateTime.now(),
  var startDate: LocalDateTime? = null,
  var endDate: LocalDateTime? = null,
  var order: Int,
  //    var tags: String, // TODO Should be a List<String>
  @ColumnInfo(name = "programID") val programID: Long? = null,
) : RealmObject {}


data class Microcycle(
  @PrimaryKey(autoGenerate = true) val microcycleID: Long = fakeULID(),
  val lazyListItemID: Long = newID(),
  val uuid: RealmUUID = RealmUUID.random(),
  var name: String,
  var description: String? = null,
  var dateCreated: LocalDateTime = LocalDateTime.now(),
  var startDate: LocalDateTime? = null,
  var endDate: LocalDateTime? = null,
  var order: Int,
  //    var tags: String, // TODO Should be a List<String>
  @ColumnInfo(name = "programID") val programID: Long? = null,
  @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
) : RealmObject {}