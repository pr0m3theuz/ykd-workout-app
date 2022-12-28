package com.example.myapplication.data

enum class Equipment {
    DUMBBELL,
    BARBELL,
    MACHINE,
    KETTLEBELL,
    BODY_WEIGHT,
    CARDIO,
    OTHER,
    NO_EQUIPMENT
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

enum class BodyParts {
    CORE,
    CALVES,
    BACK,
    BICEPS,
    CHEST,
    FOREARMS,
    GLUTES,
    HAMSTRINGS,
    LEGS,
    QUADS,
    SHOULDERS,
    TRICEPS,
    FULL_BODY
}

enum class MovementType {
    COMPOUND,
    ISOLATION,
    ISOMETRIC,
    CARDIO,
    STRETCH
}
