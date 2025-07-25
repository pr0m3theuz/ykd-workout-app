package com.example.myapplication.common.domain.model

enum class ExerciseEquipment(val printableName: String) {
    LEVER("Lever"),
    CABLE("Cable"),
    BODY_WEIGHT("Body Weight"),
    DUMBBELL("Dumbbell"),
    BARBELL("Barbell"),
    WEIGHTED("Weighted"),
    SLED("Sled"),
    SUSPENSION("Suspension"),
    SMITH("Smith"),
    BAND_ASSISTED("Band-Assisted"),
    ASSISTED("Assisted"),
    TRAP_BAR("Trap Bar"),
    SAFETY("Safety"),
    KETTLEBELL("Kettlebell"),
    SLED_SINGLE("Sled Single"),
    MEDICINE_BALL("Medicine Ball"),
    BAND_RESISTIVE("Band Resistive"),
    STRETCH("Stretch"),
}

enum class ProgressionSchemeEnum {
    WEIGHT_REPS,
    TIME,
    REPS,
    REP_RANGE,
    AMRAP,
    TIME_WEIGHT,
    DISTANCE,
    WEIGHT_DISTANCE,
    WEIGHT_DISTANCE_TIME,
}

// TODO To Implement in Realm
// TODO To Implement
// class ProgressionScheme() : RealmObject{
//    @PrimaryKey
//    lateinit var progressionSchemeID: Int
//    lateinit var name: String
//    val description: String? = null
//
// }

enum class MovementType(val printableName: String, val databaseName: String) {
    // TODO Implement in Database
    COMPOUND("Compound", "compound"),
    ISOLATION("Isolation", "isolated"),
    ISOMETRIC("Isometric", "isometric"),
    CARDIO("Cardio", "cardio"),
    STRETCH("Stretch", "stretch"),
    UNILATERAL("Unilateral", "unilateral"),
    BILATERAL("Bilateral", "bilateral"),
    PLYOMETRIC("Plyometric", "plyometric"),
    OLYMPIC("Olympic", "olympic"),
}

