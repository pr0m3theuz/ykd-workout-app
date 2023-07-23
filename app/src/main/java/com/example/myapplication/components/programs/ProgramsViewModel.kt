package com.example.myapplication.components.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Mesocycle
import com.example.myapplication.data.MesocycleTree
import com.example.myapplication.data.Microcycle
import com.example.myapplication.data.MicrocycleTree
import com.example.myapplication.data.Program
import com.example.myapplication.data.ProgramTree
import com.example.myapplication.data.ProgramsRepository
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutRepository
import com.example.myapplication.data.WorkoutTree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val programsRepository: ProgramsRepository,
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {
    private val _programs = MutableStateFlow(emptyList<Program>())
    val programs: StateFlow<List<Program>>
        get() = _programs

    private val _program = MutableStateFlow<Program?>(null)
    val program: StateFlow<Program?>
        get() = _program

    init {
        viewModelScope.launch {
            programsRepository.getAllPrograms().collect() { _programs.value = it }
        }
    }

    fun getProgramById(programID: Long) {
        viewModelScope.launch {
            programsRepository.getProgramById(programID).collect() { _program.value = it }
        }
    }

    fun insertProgram(program: Program) {
        viewModelScope.launch {
            programsRepository.insertProgram(program)
        }
    }

    fun updateProgram(program: Program) {
        viewModelScope.launch {
            programsRepository.updateProgram(program)
        }
    }

    fun deleteProgram(program: Program) {
        viewModelScope.launch {
            programsRepository.deleteProgram(program)
        }
    }

    fun getMesocyclesByProgram(programID: Long): List<Mesocycle> {
        var mesocycles = emptyList<Mesocycle>()
        viewModelScope.launch {
            mesocycles = programsRepository.getMesocyclesByProgram(programID)
        }
        return mesocycles

    }

    fun insertMesocycle(mesocycle: Mesocycle) {
        viewModelScope.launch {
            programsRepository.insertMesocycle(mesocycle)
        }
    }

    fun updateMesocycle(mesocycle: Mesocycle) {
        viewModelScope.launch {
            programsRepository.updateMesocycle(mesocycle)
        }
    }

    fun deleteMesocycle(mesocycle: Mesocycle) {
        viewModelScope.launch {
            programsRepository.deleteMesocycle(mesocycle)
        }
    }

    fun getMicrocyclesByMesocycle(mesocycleID: Long): List<Microcycle> {
        var microcycles = emptyList<Microcycle>()
        viewModelScope.launch {
            microcycles = programsRepository.getMicrocyclesByMesocycle(mesocycleID)
        }
        return microcycles

    }

    fun getWorkoutsByMicrocycle(microcycleID: Long): List<Workout> {
        var workouts = emptyList<Workout>()
        viewModelScope.launch {
            workouts = workoutRepository.getWorkoutsByMicrocycleID(microcycleID)
        }
        return workouts
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
                getWorkoutsByMicrocycle(microcycle.microcycle.microcycleID).forEach { workout ->
                    programTree.mesocyclesTree[iMesocycle].microcyclesTree[iMicrocycle].workoutsTree.add(
                        WorkoutTree(workout)
                    )
                }
            }
        }

    }


}