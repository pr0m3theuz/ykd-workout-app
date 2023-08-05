package com.example.myapplication.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class MuscleExercise() : RealmObject {
  @PrimaryKey
  var muscleExerciseID: ObjectId = ObjectId()
  var exerciseID: String? = null
  var exrxExerciseID: String? = null
  var muscleID: String? = null
  var muscleGroupID: String? = null
  var roleFunction: String? = null
}

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
class MuscleRoleFunction() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var roleFunction: String? = null
  var description: String? = null
}

class Muscle() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var muscleGroupID: String? = null
  var name: String? = null
}

class MuscleGroup() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var name: String? = null
  var exrxName: String? = null
}

class Equipment() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var exrxID: String? = null
  var name: String? = null
  var exrxName: String? = null
  var description: String? = null
}

class Utility() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var name: String? = null
  var description: String? = null
}

class Force() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var name: String? = null
  var description: String? = null
}

/**
 * Represents the biomechanics of an exercise. This class is used to store information related to
 * exercise mechanics, such as different movement patterns, techniques, or principles.
 *
 * @param id A unique identifier for each biomechanics entry.
 * @param name A descriptive name for the exercise mechanics.
 * @param description Additional information or details about the exercise mechanics (optional).
 */
class Mechanic() : RealmObject {
  @PrimaryKey
  var id: String? = null
  var name: String? = null
  var description: String? = null
}
