package com.example.myapplication.components.programs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.ExerciseSetsRepository
import com.example.myapplication.data.ExercisesRepository
import com.example.myapplication.data.Mesocycle
import com.example.myapplication.data.MesocycleTree
import com.example.myapplication.data.Microcycle
import com.example.myapplication.data.MicrocycleTree
import com.example.myapplication.data.Program
import com.example.myapplication.data.ProgramTree
import com.example.myapplication.data.ProgramsRepository
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetsRepository
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutRepository
import com.example.myapplication.data.WorkoutTree
import com.example.myapplication.data.WorkoutWithSetsAndExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel
@Inject
constructor(
  private val programsRepository: ProgramsRepository,
  private val workoutRepository: WorkoutRepository,
  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val setsRepository: SetsRepository,
  private val exercisesRepository: ExercisesRepository
) : ViewModel() {
  private val _programs = MutableStateFlow(emptyList<Program>())

  val programs: StateFlow<List<Program>>
    get() = _programs

  private val _selectedProgram = MutableStateFlow<Program?>(null)
  val selectedProgram: StateFlow<Program?>
    get() = _selectedProgram

  private val _activeProgram = MutableStateFlow<Program?>(null)
  val activeProgram: StateFlow<Program?>
    get() = _activeProgram

  init {
    viewModelScope.launch { programsRepository.getAllPrograms().collect() { _programs.value = it } }
  }

  fun getProgramById(programID: Long): Program? {
    var program: Program? = null

    viewModelScope.launch {
      programsRepository.getProgramById(programID).collect() {
        _selectedProgram.value = it
        program = it
      }
    }

    return program
  }

  fun insertProgram(program: Program) {
    viewModelScope.launch { programsRepository.insertProgram(program) }
  }

  fun updateProgram(program: Program) {
    viewModelScope.launch { programsRepository.updateProgram(program) }
  }

  fun deleteProgram(program: Program) {
    viewModelScope.launch { programsRepository.deleteProgram(program) }
  }

  fun getMesocyclesByProgram(programID: Long): List<Mesocycle> {
    var mesocycles = emptyList<Mesocycle>()
    viewModelScope.launch { mesocycles = programsRepository.getMesocyclesByProgram(programID) }
    return mesocycles
  }

  fun insertMesocycle(mesocycle: Mesocycle) {
    viewModelScope.launch { programsRepository.insertMesocycle(mesocycle) }
  }

  fun updateMesocycle(mesocycle: Mesocycle) {
    viewModelScope.launch { programsRepository.updateMesocycle(mesocycle) }
  }

  fun deleteMesocycle(mesocycle: Mesocycle) {
    viewModelScope.launch { programsRepository.deleteMesocycle(mesocycle) }
  }

  private val _selectedProgramMicrocycles = MutableStateFlow(emptyList<Microcycle>())
  val selectedProgramMicrocycle: StateFlow<List<Microcycle>>
    get() = _selectedProgramMicrocycles

  fun getMicrocyclesByProgram(programID: Long) /*: List<Microcycle>*/ {
    var microcycles = emptyList<Microcycle>()
    viewModelScope.launch {
      _selectedProgramMicrocycles.value =
        programsRepository.getMicrocyclesByProgramSorted(programID)
    }
    //    return microcycles
  }

  fun getMicrocyclesByMesocycle(mesocycleID: Long): List<Microcycle> {
    var microcycles = emptyList<Microcycle>()
    viewModelScope.launch {
      microcycles = programsRepository.getMicrocyclesByMesocycle(mesocycleID)
    }
    return microcycles
  }

  fun getWorkoutsByMicrocycle(microcycleID: Long): StateFlow<List<Workout>> {
    val workouts = MutableStateFlow(emptyList<Workout>())
    viewModelScope.launch {
      workouts.value = workoutRepository.getWorkoutsByMicrocycleID(microcycleID)
    }
    return workouts
  }

  fun getExerciseSetsByWorkout(workoutID: Long) {
    viewModelScope.launch {
      Log.i(
        "getExerciseSetsByWorkout",
        exerciseSetsRepository.getSetsWithExercise(workoutID).toString()
      )
    }
  }

  fun getWorkoutWithSetsAndExerciseByWorkoutID(
    workoutID: Long
  ): StateFlow<List<WorkoutWithSetsAndExercise>> {
    val setsAndExercise = MutableStateFlow(emptyList<WorkoutWithSetsAndExercise>())
    viewModelScope.launch {
      setsAndExercise.value = exerciseSetsRepository.getSetsAndExerciseByWorkout(workoutID)
    }
    return setsAndExercise
  }

  fun getSetsByWorkoutID(workoutID: Long): StateFlow<List<SetModel>> {
    val sets = MutableStateFlow(emptyList<SetModel>())

    viewModelScope.launch { sets.value = setsRepository.getSetsByWorkout(workoutID) }

    /*
        val exerciseIDs = sets.associateBy { it.exerciseID }.keys
        val exercises = mutableListOf<ExerciseModel>()

        exerciseIDs.forEach {
          viewModelScope.launch {
            exercises.add(getExercisesForWorkout(it))
          }
        }

        val setsGroupedByExercise = sets.groupBy { it.exerciseID }

        val exercisesSets = mutableMapOf<ExerciseModel, List<SetModel>>()
        exercises.forEach { exercise ->
          exercisesSets[exercise] = setsGroupedByExercise[exercise.exerciseID] ?: emptyList()
        }
    */

    //    Log.i("getSetsByWorkoutID()", sets.toString())
    return sets
  }

  /*fun getExerciseSetsByWorkoutID(workoutID: Long): StateFlow<MutableMap<ExerciseModel, List<SetModel>>> {
    var sets = emptyList<SetModel>()

    viewModelScope.launch {
      sets = setsRepository.getSetsByWorkout(workoutID)
    }

    val exerciseIDs = sets.associateBy { it.exerciseID }.keys
    val exercises = mutableListOf<ExerciseModel>()

    exerciseIDs.forEach {
      viewModelScope.launch {
        exercises.add(getExercisesForWorkout(it))
      }
    }

    val setsGroupedByExercise = sets.groupBy { it.exerciseID }

    val exercisesSets = mutableMapOf<ExerciseModel, List<SetModel>>()
    exercises.forEach { exercise ->
      exercisesSets[exercise] = setsGroupedByExercise[exercise.exerciseID] ?: emptyList()
    }



    println("setsAndExercise: ${exercisesSets}")
    return MutableStateFlow(exercisesSets)
  }*/

  fun getExercise(exerciseID: Long): StateFlow<List<ExerciseModel>> {
    val exercise = MutableStateFlow(emptyList<ExerciseModel>())

    viewModelScope.launch { exercise.value = exercisesRepository.getExercise(exerciseID) }

    return exercise
  }

  fun getNextWorkoutInProgram() {
    //    TODO
  }

  fun buildProgramTree(program: Program) {
    val programTree: ProgramTree = ProgramTree(program)

    getMesocyclesByProgram(programTree.program.programID).forEach() { mesocycle ->
      programTree.mesocyclesTree.add(MesocycleTree(mesocycle))
    }

    programTree.mesocyclesTree.forEachIndexed { index, mesocycle ->
      getMicrocyclesByMesocycle(mesocycle.mesocycle.mesocycleID).forEach { microcycle ->
        programTree.mesocyclesTree[index].microcyclesTree.add(MicrocycleTree(microcycle))
      }
    }

    programTree.mesocyclesTree.forEachIndexed { iMesocycle, mesocycle ->
      mesocycle.microcyclesTree.forEachIndexed { iMicrocycle, microcycle ->
        getWorkoutsByMicrocycle(microcycle.microcycle.microcycleID).value.forEach { workout ->
          programTree.mesocyclesTree[iMesocycle]
            .microcyclesTree[iMicrocycle]
            .workoutsTree
            .add(WorkoutTree(workout))
        }
      }
    }
    Log.i("buildProgramTree", programTree.mesocyclesTree.toString())
  }
}
