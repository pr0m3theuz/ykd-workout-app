@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.program

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.InProgressWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ProgramsRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProgramsViewModel
constructor(
  private val programsRepository: ProgramsRepository,
  private val inProgressWorkoutRepository: InProgressWorkoutRepository,
  // TODO Replace workoutRepository
  private val workoutRepository: WorkoutRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  //  private val setsRepository: SetsRepository,
  private val exercisesRepository: ExercisesRepository,
) : ViewModel() {
  fun getExerciseNameByID(exerciseID: ObjectId?): String? {
    //    Log.i("ProgramsViewModel", "getExerciseNameByID: ${exerciseID?.toHexString()}")
    val exercise = exercisesRepository.getExercise(exerciseID)
    //    Log.i("ProgramsViewModel", "getExerciseNameByID: ${exercise?.name}")
    return exercise?.name
  }

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow() // or just _toastMessage

  private val _programs = MutableStateFlow(emptyList<Program>())

  //  TODO Get unique list of programs, DISTINCT (name and author and dateCreated)
  val programs: StateFlow<List<Program>>
    get() = _programs

  private val _selectedProgram = MutableStateFlow<Program?>(null)
  val selectedProgram: StateFlow<Program?>
    get() = _selectedProgram

  private val _activePrograms = MutableStateFlow<List<Program>>(emptyList())
  val activePrograms: StateFlow<List<Program>>
    get() = _activePrograms

  init {
    viewModelScope.launch {
      //      programsRepository.migrateData() // Used to import data
      programsRepository.getAllDistinctProgramTemplates().collect() { _programs.value = it }
    }

    viewModelScope.launch {
      programsRepository.getActivePrograms().collect() { _activePrograms.value = it }
    }
  }

  fun getProgramById(programID: ObjectId) {
    var program: Program?

    viewModelScope.launch {
      program = programsRepository.getProgramByID(programID)
      _selectedProgram.value = program
    }
  }

  fun getCurrentlyActiveProgramByID(programID: ObjectId): Program? {
    var program: Program? = null

    viewModelScope.launch {
      program = programsRepository.getProgramByID(programID)
    }

    return program
  }

  fun isProgramActive(programUUID: Uuid): Boolean {
    return programsRepository.isProgramActive(programUUID)
  }

  fun getNextWorkoutByProgramID(programID: ObjectId): Workout? {
    return programsRepository.getNextWorkoutByProgramID(programID)
  }

  fun insertProgram(program: Program) {
    viewModelScope.launch { programsRepository.insertProgram(program) }
  }

  fun updateProgram(program: Program) {
    viewModelScope.launch { programsRepository.updateProgram(program) }
  }

  fun deleteActiveProgram(program: Program): Result<String> {
    if (program.isActive) {
      viewModelScope.launch { programsRepository.deleteActiveProgram(program) }
      _toastMessage.tryEmit("Program deleted successfully")
      return Result.success("Program deleted successfully")
    } else {
      _toastMessage.tryEmit("Error. Program could not be deleted.")
      return Result.failure(Exception("Program is not active"))
    }
  }

  fun startNewProgram(program: Program) {
    // TODO Create a copy of selected program, Keep UUID the same -> Implemented
    viewModelScope.launch {
      val result = programsRepository.createActiveProgram(program)

      if (result) {
        _toastMessage.emit("Program started successfully.")
      } else {
        _toastMessage.emit("Error starting program.")
      }
    }

    // TODO Navigate to Workouts Tab of the Active Program
    // TODO Enable Workout Start Buttons -> Implemented
    // TODO If workout completed, shaw "View" Workout button, navigate to EditWorkoutScreen ->
    // Implemented
    // TODO Navigate to Workout Screen with workoutID, getWorkoutbyID() -> Implemented
  }

  private val _selectedProgramMesocycles = MutableStateFlow(emptyList<Mesocycle>())
  val selectedProgramMesocycles: StateFlow<List<Mesocycle>>
    get() = _selectedProgramMesocycles

  fun getMesocyclesByProgram(programID: ObjectId) {
    //        var mesocycles = emptyList<Mesocycle>()
    TODO("Implement getMesocyclesByProgram")
    viewModelScope.launch {
      programsRepository.getMesocyclesByProgram(programID).collect() {
        _selectedProgramMesocycles.value = it
        _selectedProgram.value?.mesocycles = it
      }
    }
    //        return mesocycles
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
  val selectedProgramMicrocycles: StateFlow<List<Microcycle>>
    get() = _selectedProgramMicrocycles

  fun getMicrocyclesByProgram(programID: ObjectId) /*: List<Microcycle>*/ {
    TODO("Implement getMicrocyclesByProgram")
    //        var microcycles = emptyList<Microcycle>()
    Log.i("getMicrocyclesByProgram", "programID: $programID")
    viewModelScope.launch {
      programsRepository.getMicrocyclesByProgram(programID).collect() { microcycleList ->
        _selectedProgramMicrocycles.value =
          microcycleList.sortedWith(compareBy({ it.mesocycleID }, { it.order }))


        _selectedProgramMicrocycles.value.forEach { microcycle ->
          microcycle.workouts =
            workoutRepository.getWorkoutsByMicrocycleID(microcycle, microcycle.id)
        }

        microcycleList.groupBy { it.mesocycleID }.forEach {
          _selectedProgram.value?.mesocycles?.find { mesocycle -> mesocycle.id == it.key }?.microcycles =
            it.value
        }
      }
    }
  }

  fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): List<Microcycle> {
    TODO("Implement getMicrocyclesByMesocycle")
    var microcycles = emptyList<Microcycle>()
    viewModelScope.launch {
      programsRepository.getMicrocyclesByMesocycle(mesocycleID).collect() { microcycles = it }
    }
    return microcycles
  }

  private var _selectedProgramWorkouts = MutableStateFlow(mutableMapOf<Microcycle, List<Workout>>())
  val selectedProgramWorkouts: StateFlow<Map<Microcycle, List<Workout>>>
    get() = _selectedProgramWorkouts

  fun getWorkoutsByMicrocycle(microcycle: Microcycle) {
    //        val workouts = emptyList<Workout>()
    viewModelScope.launch {
      _selectedProgramWorkouts.value[microcycle] =
        workoutRepository.getWorkoutsByMicrocycleID(microcycle, microcycle.id)
    }
  }

  fun getCurrentlyActiveWorkouts(): Flow<List<Workout>> {
//    TODO "Implement getCurrentlyActiveWorkouts"
    return inProgressWorkoutRepository.getWorkouts()
  }

  fun deleteCurrentlyActiveWorkout(workoutID: ObjectId): Boolean {
//    TODO "Implement deleteCurrentlyActiveWorkout"
    return inProgressWorkoutRepository.deleteActiveWorkout(workoutID = workoutID)
  }

  fun getProgramData() {
    _selectedProgram.value?.let { getMicrocyclesByProgram(it.id) }

    _selectedProgramMicrocycles.value.forEach { getWorkoutsByMicrocycle(it) }
  }

  fun getExerciseSetsByWorkout(workoutID: Long) {
    //    viewModelScope.launch {
    //      Log.i(
    //        "getExerciseSetsByWorkout",
    //        exerciseSetsRepository.getSetsWithExercise(workoutID).toString()
    //      )
    //    }
  }

  //  fun getWorkoutWithSetsAndExerciseByWorkoutID(
  //    workoutID: Long
  //  ): StateFlow<List<WorkoutWithSetsAndExercise>> {
  //    val setsAndExercise = MutableStateFlow(emptyList<WorkoutWithSetsAndExercise>())
  //    viewModelScope.launch {
  //      setsAndExercise.value = exerciseSetsRepository.getSetsAndExerciseByWorkout(workoutID)
  //    }
  //    return setsAndExercise
  //  }

  /*  fun getSetsByWorkoutID(workoutID: Long): StateFlow<List<Set>> {
  //    val sets = MutableStateFlow(emptyList<Set>())

  //    viewModelScope.launch { sets.value = setsRepository.getSetsByWorkout(workoutID) }

      */
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
  /*

    //    Log.i("getSetsByWorkoutID()", sets.toString())
    return sets
  }*/

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

  /*  fun getExercise(exerciseID: Long): StateFlow<List<Exercise>> {
    val exercise = MutableStateFlow(emptyList<Exercise>())

    viewModelScope.launch { exercise.value = exercisesRepository.getExercise(exerciseID) }

    return exercise
  }*/

  fun getNextWorkoutInProgram() {
    //    TODO
  }

  /*fun buildProgramTree(program: Program) {
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
  }*/
}
