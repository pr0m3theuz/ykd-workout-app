@file:OptIn(ExperimentalUuidApi::class, ExperimentalStdlibApi::class)

package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi

// --- Signature Generation Functions ---

/**
 * Generates a canonical string signature for a Workout object.
 *
 * @param workout The Workout to generate a signature for.
 * @param includeTargets If true, includes target values (reps, weight, RPE/RIR) in the signature.
 * If false, only includes structural elements (exercises, set types, counts).
 * @return A string signature representing the workout.
 */
fun generateWorkoutSignature(workout: Workout, includeTargets: Boolean): String {
  val signature = StringBuilder("W[") // W for Workout start
  workout.workoutItems.forEachIndexed { index, item ->
    if (index > 0) signature.append(";") // Delimiter between WorkoutItems
    signature.append(generateWorkoutItemSignature(item, includeTargets))
  }
  signature.append("]") // W for Workout end
  return signature.toString()
}

/**
 * Generates a signature for a WorkoutItem (ExerciseSets or Superset).
 */
fun generateWorkoutItemSignature(item: WorkoutItem, includeTargets: Boolean): String {
  return when (item) {
    is ExerciseSets -> generateExerciseSetsSignature(item, includeTargets)
    is Superset -> generateSupersetSignature(item, includeTargets)
  }
}

/**
 * Generates a signature for an ExerciseSets object.
 */
fun generateExerciseSetsSignature(es: ExerciseSets, includeTargets: Boolean): String {
  val signature = StringBuilder("ES(") // ES for ExerciseSets start
  signature.append("exID:${es.exerciseID}") // Include Exercise ID
  signature.append("|sets:[") // Start sets list
  es.sets.forEachIndexed { index, set ->
    if (index > 0) signature.append(",") // Delimiter between Sets
    signature.append(generateSetSignature(set, includeTargets))
  }
  signature.append("])") // End sets list and ExerciseSets
  return signature.toString()
}

/**
 * Generates a signature for a Superset object.
 */
fun generateSupersetSignature(ss: Superset, includeTargets: Boolean): String {
  val signature = StringBuilder("SS{") // SS for Superset start
  // Optionally include Superset specific details like name or rest if needed
  // signature.append("name:${ss.name ?: "null"}|")
  signature.append("items:[") // Start inner items list
  ss.exercisesSets.forEachIndexed { index, innerEs ->
    if (index > 0) signature.append(";") // Delimiter between inner ExerciseSets
    // Recursively generate signature for inner ExerciseSets
    signature.append(generateExerciseSetsSignature(innerEs, includeTargets))
  }
  signature.append("]}") // End inner items list and Superset
  return signature.toString()
}

/**
 * Generates a signature for a single Set object.
 */
fun generateSetSignature(set: Set, includeTargets: Boolean): String {
  val signature = StringBuilder("S{") // S for Set start
  signature.append("type:${set.setTypeID.setTypeID}") // Include Set Type (using its ID)
  // Optionally include progression scheme if it's structural
  signature.append("#prog:${set.progressionSchemeID ?: "null"}")

  if (includeTargets) {
    signature.append("#trgRIR:${set.targetRepetitionsInReserve ?: "null"}")
    signature.append("#trgRPE:${set.targetRatePerceivedEffort ?: "null"}")
    signature.append("#trgE1RM:${set.targetPercentage1RM ?: "null"}")
    // Add other targets as needed (RPE, RIR, Kg, etc.)
    signature.append("#trgReps:${set.targetRepetitions ?: "null"}")
    signature.append("#trgMinReps:${set.targetMinimumRepetitions ?: "null"}")
    signature.append("#trgMaxReps:${set.targetMinimumRepetitions ?: "null"}")
    signature.append("#trgLbs:${set.targetWeightPounds ?: "null"}")
    signature.append("#trgKG:${set.targetWeightKilograms ?: "null"}")
  }
  signature.append("}") // S for Set end
  return signature.toString()
}

// --- Comparison Function ---

/**
 * Compares two workouts based on their generated signatures.
 *
 * @param w1 The first Workout object.
 * @param w2 The second Workout object.
 * @param includeTargets If true, compares signatures including target values.
 * If false, compares signatures based only on structure.
 * @return True if the signatures match, false otherwise.
 */
fun areWorkoutsIdenticalBySignature(w1: Workout, w2: Workout, includeTargets: Boolean): Boolean {
  val signature1 = generateWorkoutSignature(w1, includeTargets)
  val signature2 = generateWorkoutSignature(w2, includeTargets)
  return signature1 == signature2
}


// --- Optional: Hashing ---

/**
 * Generates a SHA-256 hash of a workout signature.
 * Useful for fixed-size representation or database indexing.
 * Requires adding a dependency for Hex encoding if not already present.
 *
 * @param workout The Workout object.
 * @param includeTargets Whether to include targets in the signature being hashed.
 * @return A SHA-256 hash string of the workout signature.
 */
// Uncomment if needed and add necessary imports/dependencies
fun generateWorkoutSignatureHash(signature: String): String {
//    val signature = generateWorkoutSignature(workout, includeTargets)
    val digest = MessageDigest.getInstance("SHA-256")
  val hashBytes = digest.digest(signature.toByteArray(Charsets.UTF_8)).toHexString()
    // Convert hashBytes to hex string (requires a helper function or library)
  return hashBytes // Replace bytesToHex with actual implementation
}

// Example helper (replace with a robust implementation or library)
/*private fun bytesToHex(bytes: ByteArray): String {
    return bytes.joinToString("") { "%02x".format(it) }
}

fun areWorkoutsIdenticalByHash(w1: Workout, w2: Workout, includeTargets: Boolean): Boolean {
    val hash1 = generateWorkoutSignatureHash(w1, includeTargets)
    val hash2 = generateWorkoutSignatureHash(w2, includeTargets)
    return hash1 == hash2
}*/
