package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MuscleExerciseDao(var MuscleExercise: MuscleExercise)

@Serializable
data class MuscleRoleFunctionDao(var MuscleRoleFunction: MuscleRoleFunction)

@Serializable
data class MuscleDao(var Muscle: Muscle)

@Serializable
data class MuscleGroupDao(var MuscleGroup: MuscleGroup)

@Serializable
data class EquipmentDao(var Equipment: Equipment)

@Serializable
data class UtilityDao(var Utility: Utility)

@Serializable
data class ForceDao(var Force: Force)

@Serializable
data class MechanicDao(var Mechanic: Mechanic)

@Serializable
data class MuscleExercise(
    @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
    var exerciseID: String,
    var muscleID: String,
    var muscleGroupID: String,
    var roleFunction: String,
    var type: String? = null,
)

/**
 * Represents a muscle's role or function in an exercise. This class is used to store information
 * about the roles or functions a muscle can have in an exercise.
 *
 * @param id A unique identifier for each muscle function exercise relation.
 * @param roleFunction The role or function of the muscle in the exercise.
 * @param description Additional information or details about the muscle function (optional).
 *
 * In the context of muscle roles or functions in an exercise, the following descriptions can be
 * provided:
 * - Target: The primary muscle group responsible for generating movement or force during the
 *   exercise.
 * - Synergist: Secondary muscles that assist the target muscle in generating movement.
 * - Stabilizer: Muscles responsible for maintaining stability and supporting the body or specific
 *   joints.
 * - Dynamic Stabilizer: Muscles that stabilize a joint while also generating movement at another
 *   joint.
 * - Oblique Stabilizer: Muscles that provide stability and support to the oblique abdominal
 *   muscles.
 */
@Serializable
data class MuscleRoleFunction(
    @SerialName("id") var id: String,
    var roleFunction: String,
    var description: String,
    var type: String? = null,
)

@Serializable
data class Muscle(
    @SerialName("id") var id: String,
    var muscleGroupID: String,
    var name: String,
    var type: String? = null,
)

@Serializable
data class MuscleGroup(
    @SerialName("id") var id: String,
    var name: String,
    var exrxName: String,
    var type: String? = null,
)

@Serializable
data class Equipment(
    @SerialName("id") var id: String,
    var exrxID: String,
    var name: String,
    var exrxName: String,
    var description: String,
    var type: String? = null,
)

@Serializable
data class Utility(
    @SerialName("id") var id: String,
    var name: String,
    var description: String,
    var type: String? = null,
)

@Serializable
data class Force(
    @SerialName("id") var id: String,
    var name: String,
    var description: String,
    var type: String? = null,
)

/**
 * Represents the biomechanics of an exercise. This class is used to store information related to
 * exercise mechanics, such as different movement patterns, techniques, or principles.
 *
 * @param id A unique identifier for each biomechanics entry.
 * @param name A descriptive name for the exercise mechanics.
 * @param description Additional information or details about the exercise mechanics (optional).
 */
@Serializable
data class Mechanic(
    @SerialName("id") var id: String,
    var name: String,
    var description: String,
    var type: String? = null,
)
