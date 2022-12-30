package com.example.myapplication.data

enum class Equipment(val printableName: String) {
    DUMBBELL("Dumbbell"),
    BARBELL("Barbell"),
    MACHINE("Machine"),
    KETTLEBELL("Kettlebell"),
    BODY_WEIGHT("Body Weight"),
    CARDIO("Cardio"),
    OTHER("Other"),
    NO_EQUIPMENT("No Equipment")
}

enum class ExerciseProgression {
    WEIGHT_REPS,
    TIME,
    REPS,
    TIME_WEIGHT,
    DISTANCE,
    WEIGHT_DISTANCE,
    WEIGHT_DISTANCE_TIME
}

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
    QUADS("Quad"),
    SHOULDERS("Shoulders"),
    TRICEPS("Triceps"),
    FULL_BODY("Full Body")
}

enum class MovementType(val printableName: String) {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    ISOMETRIC("Isometric"),
    CARDIO("Cardio"),
    STRETCH("Stretch")
}
