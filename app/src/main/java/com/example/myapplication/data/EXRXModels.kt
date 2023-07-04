package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "muscleExercises",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["exerciseID"],
            childColumns = ["exerciseID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Muscle::class,
            parentColumns = ["id"],
            childColumns = ["muscleID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MuscleGroup::class,
            parentColumns = ["id"],
            childColumns = ["muscleGroupID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MuscleRoleFunction::class,
            parentColumns = ["id"],
            childColumns = ["roleFunction"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MuscleExercise(
    @PrimaryKey(autoGenerate = true) val muscleExerciseID: Int,
    @ColumnInfo(name = "exerciseID") val exerciseID: String,
    @ColumnInfo(name = "exrxExerciseID") val exrxExerciseID: String,
    @ColumnInfo(name = "muscleID") val muscleID: String,
    @ColumnInfo(name = "muscleGroupID") val muscleGroupID: String,
    @ColumnInfo(name = "roleFunction") val roleFunction: String,
)

/**
 * Represents a muscle's role or function in an exercise.
 * This data class is used to store information about the roles or functions a muscle can have
 * in an exercise.
 *
 * @param id A unique identifier for each muscle function exercise relation.
 * @param roleFunction The role or function of the muscle in the exercise.
 * @param description Additional information or details about the muscle function (optional).
 *
 * In the context of muscle roles or functions in an exercise, the following descriptions can be provided:
 * - Target: The primary muscle group responsible for generating movement or force during the exercise.
 * - Synergist: Secondary muscles that assist the target muscle in generating movement.
 * - Stabilizer: Muscles responsible for maintaining stability and supporting the body or specific joints.
 * - Dynamic Stabilizer: Muscles that stabilize a joint while also generating movement at another joint.
 * - Oblique Stabilizer: Muscles that provide stability and support to the oblique abdominal muscles.
 */
@Entity(tableName = "muscleRolesFunctions")
data class MuscleRoleFunction(
    @PrimaryKey val id: String,
    val roleFunction: String,
    val description: String?,
)

@Entity(
    tableName = "muscles",
    foreignKeys = [
        ForeignKey(
            entity = MuscleGroup::class,
            parentColumns = ["id"],
            childColumns = ["muscleGroupID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Muscle(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "muscleGroupID") val muscleGroupID: String,
    val name: String,
)

@Entity(tableName = "muscleGroups")
data class MuscleGroup(
    @PrimaryKey val id: String,
    val name: String,
)

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey val id: String,
    val name: String,
)

@Entity(tableName = "utility")
data class Utility(
    @PrimaryKey val id: String,
    val name: String,
)

@Entity(tableName = "forces")
data class Force(
    @PrimaryKey val id: String,
    val name: String,
)

/**
 * Represents the biomechanics of an exercise.
 * This data class is used to store information related to exercise mechanics,
 * such as different movement patterns, techniques, or principles.
 *
 * @param id A unique identifier for each biomechanics entry.
 * @param name A descriptive name for the exercise mechanics.
 * @param description Additional information or details about the exercise mechanics (optional).
 */
@Entity(tableName = "mechanics")
data class Mechanic(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
)
