package com.example.myapplication.components

import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutRepository
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import org.mongodb.kbson.ObjectId

class PostWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    val currentUser = userRepository.getCurrentUser()
    val userDefaultWeightUnit: String =
        userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
            ?: MassUnit.POUNDS.abbreviation

    fun getCompletedWorkout(workoutID: ObjectId): Workout? {
        return workoutRepository.getWorkoutByID(workoutID)
    }

    fun getNextWorkout() {

    }
}
