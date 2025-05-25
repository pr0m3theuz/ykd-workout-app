@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.infrastructure.persistence.datastore

import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType // Assuming SetType has a 'name' or similar property
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.datastore.ActiveWorkout
import com.example.myapplication.datastore.ExerciseNoteProto
import com.example.myapplication.datastore.ExerciseSetsProto
import com.example.myapplication.datastore.SetProto
import com.example.myapplication.datastore.SupersetProto
import com.example.myapplication.datastore.WorkoutItemProto
import com.example.myapplication.datastore.WorkoutNoteProto
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant as KotlinxInstant

// --- Primitive Type Mappers ---

fun ObjectId.toProtoString(): String = this.toHexString()
fun String.toDomainObjectId(): ObjectId = ObjectId(this)

fun Uuid.toProtoString(): String = this.toString()
fun String.toDomainUuid(): Uuid = Uuid.parse(this)

fun KotlinxInstant.toProtoLong(): Long = this.toEpochMilliseconds()
fun Long.toDomainInstant(): KotlinxInstant = KotlinxInstant.fromEpochMilliseconds(this)

// --- Enum Mappers (Example for SetType) ---
// Assuming SetType has a 'name' property or similar unique string identifier.
// Adjust if SetType uses ordinal or a specific ID.
fun SetType.toProtoString(): String =
    this.setTypeID.toString() // Or this.id, this.ordinal.toString()
fun String.toDomainSetType(): SetType {
    // This might require a lookup map or a when statement if SetType is more complex
    // For simplicity, assuming SetType.valueOf(name) works or there's a similar factory method.
    // If SetType has a companion object with a fromString method:
    // return SetType.fromString(this)
    // For now, let's assume a simple 'valueOf', which might not be robust for all enum cases.
    // You'll likely need a more specific implementation based on your SetType definition.
    // Placeholder:
    return SetType.getSetType(this.toInt()) // Default if not found
}


// --- Note Mappers ---

fun WorkoutNote.toProto(): WorkoutNoteProto {
    val builder = WorkoutNoteProto.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListID = this.lazyListID
    builder.uuid = this.uuid.toProtoString()
    this.workout?.let { builder.workout = it.toProtoString() }
    this.workoutUUID?.let { builder.workoutUUID = it.toProtoString() }
    this.note?.let { builder.note = it }
    builder.pinned = this.pinned
    this.ownerID?.let { builder.ownerID = it }
    // isPublic is not in domain WorkoutNote, defaulting to false or consider adding it
//    builder.isPublic = false // Default value
    return builder.build()
}

fun WorkoutNoteProto.toDomain(): WorkoutNote {
    val domain = WorkoutNote()
    domain.id = this.id.toDomainObjectId()
    domain.lazyListID = this.lazyListID
    domain.uuid = this.uuid.toDomainUuid()
    domain.workout = this.workout.toDomainObjectId()
    domain.workoutUUID = this.workoutUUID.toDomainUuid()
    domain.note = this.note
    domain.pinned = this.pinned
    domain.ownerID = this.ownerID
    // isPublic is not in domain WorkoutNote, so it's ignored or handled if domain model changes
    return domain
}

fun ExerciseNote.toProto(): ExerciseNoteProto {
    val builder = ExerciseNoteProto.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListID = this.lazyListID
    builder.uuid = this.uuid.toProtoString()
    this.exercise?.let { builder.exercise = it.toProtoString() }
    this.workout?.let { builder.workout = it.toProtoString() }
    this.note?.let { builder.note = it }
    builder.pinned = this.pinned
    this.ownerID?.let { builder.ownerID = it }
    // isPublic is not in domain ExerciseNote, defaulting to false
//    builder.isPublic = false // Default value
    // dateCreated is in domain but not in proto ExerciseNoteProto
    return builder.build()
}

fun ExerciseNoteProto.toDomain(): ExerciseNote {
    val domain = ExerciseNote()
    domain.id = this.id.toDomainObjectId()
    domain.lazyListID = this.lazyListID
    domain.uuid = this.uuid.toDomainUuid()
    domain.exercise = this.exercise.toDomainObjectId()
    domain.workout = this.workout.toDomainObjectId()
    domain.note = this.note
    domain.pinned = this.pinned
    domain.ownerID = this.ownerID
    // isPublic is not in domain ExerciseNote
    // dateCreated is in domain but not in proto, will take default in domain
    return domain
}

// --- Set Mapper ---

fun Set.toProto(): SetProto {
    val builder = SetProto.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListItemID = this.lazyListItemID
    builder.uuid = this.uuid.toProtoString()
    this.setNumber?.let { builder.setNumber = it }
    this.weightPounds?.let { builder.weightPounds = it }
    this.weightKilograms?.let { builder.weightKilograms = it }
    this.repetitions?.let { builder.repetitions = it }
    this.ratePerceivedEffort?.let { builder.ratePerceivedEffort = it }
    this.repetitionsInReserve?.let { builder.repetitionsInReserve = it }
    builder.setTypeID = this.setTypeID.toProtoString() // Assuming SetType has a 'name' or similar property
    this.percentage1RM?.let { builder.percentage1RM = it }
    builder.setCompleted = this.setCompleted
    // restTimerInSeconds not in domain Set, default or map if available elsewhere
    // notes (String) not in domain Set, default or map if available elsewhere
    builder.exerciseID = this.exerciseID?.toProtoString() ?: "" // Proto expects non-null string
    builder.exerciseSetsID = this.exerciseSetsID.toProtoString()
    builder.workoutID = this.workoutID.toProtoString()
    builder.ownerID = this.ownerID
    // order is in proto but not in domain Set
    return builder.build()
}

fun SetProto.toDomain(): Set {
    val domain = Set(
        id = this.id.toDomainObjectId(),
        lazyListItemID = this.lazyListItemID,
        uuid = this.uuid.toDomainUuid(),
        workoutID = this.workoutID.toDomainObjectId(),
        exerciseSetsID = this.exerciseSetsID.toDomainObjectId(),
        ownerID = this.ownerID
    )
    if (this.hasSetNumber()) domain.setNumber = this.setNumber
    if (this.hasWeightPounds()) domain.weightPounds = this.weightPounds
    if (this.hasWeightKilograms()) domain.weightKilograms = this.weightKilograms
    if (this.hasRepetitions()) domain.repetitions = this.repetitions
    if (this.hasRatePerceivedEffort()) domain.ratePerceivedEffort = this.ratePerceivedEffort
    if (this.hasRepetitionsInReserve()) domain.repetitionsInReserve = this.repetitionsInReserve
    domain.setTypeID = this.setTypeID.toDomainSetType()
    if (this.hasPercentage1RM()) domain.percentage1RM = this.percentage1RM
    domain.setCompleted = this.setCompleted
    domain.exerciseID = this.exerciseID.toDomainObjectId()
    // restTimerInSeconds not in domain Set
    // notes (String) not in domain Set
    // order not in domain Set
    return domain
}

// --- ExerciseSets and Superset Mappers ---

fun ExerciseSets.toProto(): ExerciseSetsProto {
    val builder = ExerciseSetsProto.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListItemID = this.lazyListItemID
    builder.uuid = this.exSetsUUID.toProtoString() // Domain uses exSetsUUID
    builder.exerciseID = this.exerciseID.toProtoString()
    builder.workoutID = this.workoutID.toProtoString()
    builder.addAllNotes(this.notes.map { it.toProto() })
    builder.addAllSets(this.sets.map { it.toProto() })
    builder.ownerID = this.ownerID
    builder.order = this.order
    this.microcycleID?.let { builder.microcycleID = it.toProtoString() }
    this.mesocycleID?.let { builder.mesocycleID = it.toProtoString() }
    this.programID?.let { builder.programID = it.toProtoString() }
    return builder.build()
}

fun ExerciseSetsProto.toDomain(): ExerciseSets {
    return ExerciseSets(
        id = this.id.toDomainObjectId(),
        lazyListItemID = this.lazyListItemID,
        exSetsUUID = this.uuid.toDomainUuid(), // Proto uses uuid
        exerciseID = this.exerciseID.toDomainObjectId(),
        workoutID = this.workoutID.toDomainObjectId(),
        notes = this.notesList.map { it.toDomain() }.toMutableList(),
        sets = this.setsList.map { it.toDomain() }.toMutableList(),
        ownerID = this.ownerID,
        order = this.order,
        programID = if (this.hasProgramID() && this.programID != "") this.programID.toDomainObjectId() else null,
        mesocycleID = if (this.hasMesocycleID() && this.mesocycleID != "") this.mesocycleID.toDomainObjectId() else null,
        microcycleID = if (this.hasMicrocycleID() && this.microcycleID != "") this.microcycleID.toDomainObjectId() else null,
        // isPublic is in domain but not in proto ExerciseSetsProto (it's in WorkoutItem)
    )
}

fun Superset.toProto(): SupersetProto {
    val builder = SupersetProto.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListItemID = this.lazyListItemID
    builder.uuid = this.uuid.toProtoString()
    builder.addAllExercisesSets(this.exercisesSets.map { it.toProto() })
    builder.workoutID = this.workoutID.toProtoString()
    this.ownerID?.let { builder.ownerID = it } // ownerID is nullable in domain Superset
    builder.order = this.order
    this.programID?.let { builder.programID = it.toProtoString() }
    this.mesocycleID?.let { builder.mesocycleID = it.toProtoString() }
    this.microcycleID?.let { builder.microcycleID = it.toProtoString() }
    // isPublic is in domain but not in proto SupersetProto (it's in WorkoutItem)
    // supersetNotes in domain, not in proto
    return builder.build()
}

fun SupersetProto.toDomain(): Superset {
    return Superset(
        id = this.id.toDomainObjectId(),
        lazyListItemID = this.lazyListItemID,
        uuid = this.uuid.toDomainUuid(),
        exercisesSets = this.exercisesSetsList.map { it.toDomain() },
        workoutID = this.workoutID.toDomainObjectId(),
        ownerID = this.ownerID,
        order = this.order,
        programID = if (this.hasProgramID() && this.programID != "") this.programID.toDomainObjectId() else null,
        mesocycleID = if (this.hasMesocycleID() && this.mesocycleID != "") this.mesocycleID.toDomainObjectId() else null,
        microcycleID = if (this.hasMicrocycleID() && this.microcycleID != "") this.microcycleID.toDomainObjectId() else null,
        // isPublic is in domain but not in proto SupersetProto (it's in WorkoutItem)
        // supersetNotes in domain, not in proto
    )
}

// --- WorkoutItem Mapper ---

fun WorkoutItem.toProto(): WorkoutItemProto {
    val builder = WorkoutItemProto.newBuilder()
    when (this) {
        is ExerciseSets -> builder.exerciseSets = this.toProto()
        is Superset -> builder.superset = this.toProto()
    }
    return builder.build()
}

fun WorkoutItemProto.toDomain(): WorkoutItem {
    return when (this.itemCase) {
        WorkoutItemProto.ItemCase.EXERCISESETS -> this.exerciseSets.toDomain()
        WorkoutItemProto.ItemCase.SUPERSET -> this.superset.toDomain()
        WorkoutItemProto.ItemCase.ITEM_NOT_SET, null -> throw IllegalArgumentException("WorkoutItemProto item not set")
    }
}

// --- Workout (ActiveWorkout) Mapper ---

fun Workout.toProtoActiveWorkout(): ActiveWorkout {
    val builder = ActiveWorkout.newBuilder()
    builder.id = this.id.toProtoString()
    builder.lazyListItemID = this.lazyListItemID
    builder.uuid = this.uuid.toProtoString()
    builder.name = this.name
    this.workoutAuthor?.let { builder.workoutAuthor = it }
    builder.addAllNotes(this.notes.map { it.toProto() })
    builder.workoutDuration = this.workoutDuration
    this.workoutType?.let { builder.workoutType = it }
    builder.dateCreated = this.dateCreated.toProtoLong()
    this.workoutStarted?.let { builder.workoutStarted = it.toProtoLong() }
    this.workoutEnded?.let { builder.workoutEnded = it.toProtoLong() }
    builder.isCompleted = this.isCompleted
    this.day?.let { builder.day = it }
    builder.addAllWorkoutItems(this.workoutItems.map { it.toProto() })
    this.totalVolumeInPounds?.let { builder.totalVolumeInPounds = it }
    this.totalVolumeInKilograms?.let { builder.totalVolumeInKilograms = it }
    this.programID?.let { builder.programID = it.toProtoString() }
    this.mesocycleID?.let { builder.mesocycleID = it.toProtoString() }
    this.microcycleID?.let { builder.microcycleID = it.toProtoString() }
    builder.ownerID = this.ownerID
    builder.isPublic = this.isPublic
    this.signature?.let { builder.signature = it }
    this.hashedSignature?.let { builder.hashedSignature = it }
    return builder.build()
}

fun ActiveWorkout.toDomainWorkout(): Workout {
    return Workout(
        id = this.id.toDomainObjectId(),
        lazyListItemID = this.lazyListItemID,
        uuid = this.uuid.toDomainUuid(),
        name = this.name,
        workoutAuthor = if (this.hasWorkoutAuthor()) this.workoutAuthor else null,
        notes = this.notesList.map { it.toDomain() },
        workoutDuration = this.workoutDuration,
        workoutType = if (this.hasWorkoutType()) this.workoutType else null,
        dateCreated = this.dateCreated.toDomainInstant(),
        workoutStarted = if (this.hasWorkoutStarted()) this.workoutStarted.toDomainInstant() else null,
        workoutEnded = if (this.hasWorkoutEnded()) this.workoutEnded.toDomainInstant() else null,
        isCompleted = this.isCompleted,
        day = if (this.hasDay()) this.day else null,
        workoutItems = this.workoutItemsList.map { it.toDomain() },
        totalVolumeInPounds = if (this.hasTotalVolumeInPounds()) this.totalVolumeInPounds else null,
        totalVolumeInKilograms = if (this.hasTotalVolumeInKilograms()) this.totalVolumeInKilograms else null,
        programID = if (this.hasProgramID()) this.programID.toDomainObjectId() else null,
        mesocycleID = if (this.hasMesocycleID()) this.mesocycleID.toDomainObjectId() else null,
        microcycleID = if (this.hasMicrocycleID()) this.microcycleID.toDomainObjectId() else null,
        ownerID = this.ownerID,
        isPublic = this.isPublic,
        signature = if (this.hasSignature()) this.signature else null,
        hashedSignature = if (this.hasHashedSignature()) this.hashedSignature else null
    )
}
