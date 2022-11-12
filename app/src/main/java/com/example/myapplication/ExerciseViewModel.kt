package com.example.myapplication

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel

class ExerciseViewModel : ViewModel() {

    private val _exerciseSets = initExerciseSets().toMutableStateList()
    val exerciseSets: List<SetModel>
        get() = _exerciseSets

    fun addSet() {
        if (_exerciseSets.size > 0) {
            _exerciseSets.add(
                _exerciseSets.last().copy(
                    id = newID(),
                    setCompleted = false
                )
            )
        } else {
            _exerciseSets.add(
                SetModel()
            )
        }
    }

    fun remove(id: Long) {
//        for (index in 0 until _exerciseSets.size) {
//            if (exerciseSets[index].id == id) {
//                _exerciseSets.removeAt(index)
//                break
//            }
//        }
        _exerciseSets.removeIf { it.id == id }
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(id: Long, newReps: Int?) {
        _exerciseSets.forEach { if (it.id == id) it.repetitions = newReps }
    }

    fun updateSetRIR(id: Long, newRIR: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.ratePerceivedEffort = newRIR }
    }

    fun updateSetRPE(id: Long, newRPE: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.ratePerceivedEffort = newRPE }
    }

    fun setComplete(id: Long, setComplete: Boolean) {
        _exerciseSets.forEach { if (it.id == id) it.setCompleted = setComplete }
    }

    fun updateSetType(id: Long, setType: SetTypeModel) {
        _exerciseSets.forEach { if (it.id == id) it.setType = setType }
    }

    fun addWarmUpSets(warmUpSets: List<SetModel>) {
        for (index in warmUpSets.indices) {
            _exerciseSets.add(index, warmUpSets[index].copy(id = newID()))
        }
    }

    fun getWorkingSetWeight(): Double? {
//        for (index in _exerciseSets.indices) {
//            if (_exerciseSets[index].setType.name != "Warm Up Set") {
//                return _exerciseSets[index].exerciseWeight
//                break
//            }
//        }
//        return null
        // TODO Add If Not Empty
        var workingSetWeight =
            _exerciseSets.filter { it.setType.name != "Warm Up Set" }.first().exerciseWeight
        if (workingSetWeight != null) {
            return workingSetWeight
        } else {
            return null
        }
    }

    fun addWorkingSets(workingSets: List<SetModel>) {
//        for (index in _exerciseSets.indices) {
//            if (_exerciseSets[index].setType.name != "Warm Up Set") {
//                _exerciseSets.removeAt(index = index)
//            }
//        }
        workingSets.forEach { it.id = newID() }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Before: ${_exerciseSets.toList()}")
        _exerciseSets.removeAll { it.setType.name != "Warm Up Set" }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Removed Old Sets: ${_exerciseSets.toList()}")
        _exerciseSets.addAll(workingSets)
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Added New Sets: ${_exerciseSets.toList()}")
    }

    fun addBackOffSets(backOffSets: List<SetModel>) {
        for (index in backOffSets.indices) {
            _exerciseSets.add(backOffSets[index].copy(id = newID()))
        }

//        backOffSets.forEach { it.id = newID() }
//        _exerciseSets.addAll(backOffSets)
    }
}

private fun initExerciseSets(): List<SetModel> {
    return List(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}
