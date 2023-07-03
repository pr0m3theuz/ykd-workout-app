package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "muscle_exercise",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["exrxID"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Muscle::class,
            parentColumns = ["id"],
            childColumns = ["muscle_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MuscleGroup::class,
            parentColumns = ["id"],
            childColumns = ["muscle_group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MuscleExerciseRelation::class,
            parentColumns = ["id"],
            childColumns = ["relation"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MuscleExercise(
    @ColumnInfo(name = "exercise_id") val exerciseID: String,
    @ColumnInfo(name = "muscle_id") val muscleID: String,
    @ColumnInfo(name = "muscle_group_id") val muscleGroupID: String,
    val relation: String
)

@Entity(tableName = "muscle_exercise_relation")
data class MuscleExerciseRelation(
    @PrimaryKey val id: String
)

@Entity(
    tableName = "muscle",
    foreignKeys = [
        ForeignKey(
            entity = MuscleGroup::class,
            parentColumns = ["id"],
            childColumns = ["muscle_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Muscle(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "muscle_group_id") val muscleGroupId: String,
    val name: String
)

/*
@Entity(tableName = "muscle_group")
data class MuscleGroup(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey val id: String,
    val name: String
)*/
