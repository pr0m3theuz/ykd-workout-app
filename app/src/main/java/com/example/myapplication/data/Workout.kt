package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["programID"],
            childColumns = ["programID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Mesocycle::class,
            parentColumns = ["mesocycleID"],
            childColumns = ["mesocycleID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Microcycle::class,
            parentColumns = ["microcycleID"],
            childColumns = ["microcycleID"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val workoutID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var workoutAuthor: String? = null,
//    TODO Remove. Deprecated.
    var notes: String? = null,
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
//
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds,
    var workoutType: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    val workoutStarted: LocalDateTime = LocalDateTime.now(),
    var workoutEnded: LocalDateTime? = null,
    var day: Int? = null,
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
    @ColumnInfo(name = "microcycleID") val microcycleID: Long? = null,
)

//fun Workout.pinNotes(pinned: Boolean) {
//    notesPinned = pinned
//    Log.i("pinNotes()", notesPinned.toString())
//}

@Entity(primaryKeys = ["workoutID", "exerciseSetsID"/*, "exerciseNotePrimaryKey"*/])
data class WorkoutExerciseSetsCrossRef(
    val workoutID: Long,
    val exerciseSetsID: Long,
)

data class WorkoutWithExercisesSets(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutID",
        entityColumn = "exerciseSetsID",
        associateBy = Junction(WorkoutExerciseSetsCrossRef::class)
    )
    val exercisesSets: List<ExerciseSets>,
)
