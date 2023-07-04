package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
TODO Remove
enum class Equipment(val printableName: String) {
    DUMBBELL("Dumbbell"),
    BARBELL("Barbell"),
    PLATE_LOADED_MACHINE("Plate Loaded"),
    MACHINE("Machine"),
    KETTLEBELL("Kettlebell"),
    MEDICINEBALL("Medicine Ball"),
    BODY_WEIGHT("Body Weight"),
    CARDIO("Cardio"),
    OTHER("Other"),
    NO_EQUIPMENT("No Equipment"),
}
*/

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

@Entity(tableName = "progressionSchemes")
data class ProgressionScheme(
    @PrimaryKey val progressionSchemeID: Int,
    val name: String,
    val description: String? = null,
)

/*
TODO Remove
enum class MuscleGroup(val printableName: String) {
    CORE("Core"),
    CALVES("Calves"),
    BACK("Back"),
    BICEPS("Biceps"),
    CHEST("Chest"),
    FOREARMS("Forearms"),
    GLUTES("Glutes"),
    HAMSTRINGS("Hamstring"),
    LEGS("Legs"),
    NECK("Neck"),
    QUADS("Quad"),
    SHOULDERS("Shoulders"),
    TRICEPS("Triceps"),
    FULL_BODY("Full Body"),
}*/

enum class MovementType(val printableName: String) {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    ISOMETRIC("Isometric"),
    CARDIO("Cardio"),
    STRETCH("Stretch"),
    UNILATERAL("Unilateral"),
    BILATERAL("Bilateral"),
    PLYOMETRIC("Plyometric"),
    OLYMPIC("Olympic"),
}

/*
TODO Remove
enum class ExerciseForce(val printableName: String) {
    PUSH("Push"),
    PULL("Pull"),
}
*/
