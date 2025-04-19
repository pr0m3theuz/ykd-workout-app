package com.example.myapplication.features.workout.feature.exercise

class ExerciseWeightState(weight: String? = null) :
    TextFieldState() {
    init {
        weight?.let {
            text = it
        }
    }
}

val ExerciseWeightStateSaver = textFieldStateSaver(ExerciseWeightState())

class ExerciseRepetitionsState(repetitions: String? = null) :
    TextFieldState() {
    init {
        repetitions?.let {
            text = it
        }
    }
}

val ExerciseRepetitionsStateSaver = textFieldStateSaver(ExerciseRepetitionsState())

class ExerciseRIRState(rir: String? = null) :
    TextFieldState() {
    init {
        rir?.let {
            text = it
        }
    }
}

val ExerciseRIRStateSaver = textFieldStateSaver(ExerciseRIRState())
