package com.example.myapplication.components.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.ProgramsRepository
import com.example.myapplication.data.network.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class ProgramsViewModel
constructor(
    private val programsRepository: ProgramsRepository,
    private val workoutRepository: WorkoutRepository,
//  private val exerciseSetsRepository: ExerciseSetsRepository,
    //  private val setsRepository: SetsRepository,
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
        viewModelScope.launch {
            programsRepository.getAllPrograms().collect() { _programs.value = it }
        }
    }

    fun getProgramById(programID: ObjectId): Program? {
        var program: Program? = null

        viewModelScope.launch {
            program = programsRepository.getProgramById(programID)
            _selectedProgram.value = program
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

    fun getMesocyclesByProgram(programID: ObjectId): List<Mesocycle> {
        var mesocycles = emptyList<Mesocycle>()
        viewModelScope.launch {
            programsRepository.getMesocyclesByProgram(programID).collect() {
                mesocycles = it
            }
        }
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

    fun getMicrocyclesByProgram(programID: ObjectId) /*: List<Microcycle>*/ {
        var microcycles = emptyList<Microcycle>()
        viewModelScope.launch {
            programsRepository.getMicrocyclesByProgram(programID)
                .collect() { _selectedProgramMicrocycles.value = it }
        }
        //    return microcycles
    }

    fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): List<Microcycle> {
        var microcycles = emptyList<Microcycle>()
        viewModelScope.launch {
            programsRepository.getMicrocyclesByMesocycle(mesocycleID).collect() { microcycles = it }
        }
        return microcycles
    }

    fun getWorkoutsByMicrocycle(microcycleID: ObjectId): StateFlow<List<Workout>> {
        val workouts = MutableStateFlow(emptyList<Workout>())
        viewModelScope.launch {
            workoutRepository.getWorkoutsByMicrocycleID(microcycleID)
                .collect() { workouts.value = it }
        }
        return workouts
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
