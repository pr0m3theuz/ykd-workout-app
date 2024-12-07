package com.example.myapplication.components

import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.ProgramsRepository
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutRepository
import com.example.myapplication.utils.ProgressionType
import com.example.myapplication.utils.selectProgression
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import org.mongodb.kbson.ObjectId

class PostWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val programRepository: ProgramsRepository,
    private val userRepository: UserRepository,
) {
    val currentUser = userRepository.getCurrentUser()
    val userDefaultWeightUnit: String =
        userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
            ?: MassUnit.POUNDS.abbreviation

    fun getCompletedWorkout(workoutID: ObjectId): Workout? {
        return workoutRepository.getWorkoutByID(workoutID)
    }

    suspend fun updateNextWorkoout(workout: Workout) {
        workoutRepository.updateWorkout(workout)
    }

    fun getNextWorkout(workout: Workout, updateSets: (MutableList<Set>) -> Unit): Workout? {
        // TODO get next exercise set in program with the same min, max reps, and progression type
//        val nextWorkout = programRepository.getNextWorkoutByProgramID(workout.programID!!)
        var workoutToUpdate: Workout? = null
        workout.exercisesSets.forEach { exerciseSets ->

            val nextWorkout =
                workoutRepository.findNextWorkoutExerciseSets(exerciseSets, workout.programID!!)

            if (nextWorkout != null) {
                // TODO Might change every loop, address this
                workoutToUpdate = nextWorkout


                val exerciseSetsToUpdate =
                    nextWorkout.exercisesSets?.firstOrNull { exerciseSetsToUpdate -> exerciseSetsToUpdate.exerciseID == exerciseSets.exerciseID }

                var updatedSets = mutableListOf<Set>()

                if (exerciseSetsToUpdate != null) {

                    exerciseSets.sets.forEach { set ->
                        set.progressionSchemeID?.let {
                            ProgressionType.fromProgression(it)
                                ?.let { progressionType ->
                                    val progressionFunction: (List<Set>, MutableList<Set>) -> Result<MutableList<Set>> =
                                        selectProgression(progressionType)

                                    // TODO Handle success and failure cases of progression function

                                    updatedSets = progressionFunction(
                                        exerciseSets.sets.toList(),
                                        exerciseSetsToUpdate.sets
                                    ).getOrNull() ?: mutableListOf<Set>()

                                }
                        }
                        // TODO update matching exerciseSets in workoutToUpdate
                        if (updatedSets.isNotEmpty()) {
                            workoutToUpdate!!.exercisesSets.firstOrNull { it.exerciseID == exerciseSetsToUpdate.exerciseID }?.sets =
                                updatedSets.toRealmList()
                        }

                    }
                }
            }
        }
        return workoutToUpdate
    }
}
