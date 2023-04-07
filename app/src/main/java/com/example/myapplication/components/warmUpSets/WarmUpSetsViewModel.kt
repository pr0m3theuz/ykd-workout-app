package com.example.myapplication.components.warmUpSets

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.newID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode

class WarmUpSetsViewModel : ViewModel() {
    private var _workingSetWeight = MutableStateFlow(100.0)
    val workingSetWeight
        get() = _workingSetWeight.asStateFlow()

    private val _warmUpSets = MutableStateFlow(initWarmUpSets())
    val warmUpSets
        get() = _warmUpSets.asStateFlow()

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _workingSetWeight.value = newWorkingWeight
    }

    fun addSet() {
        if (_warmUpSets.value.isNotEmpty()) {
            _warmUpSets.value.add(
                _warmUpSets.value.last().copy(
                    id = newID()
                )
            )
        } else {
            _warmUpSets.value.add(
                SetModel()
            )
        }
    }

    fun remove(id: Long) {
        for (index in _warmUpSets.value.indices) {
            if (_warmUpSets.value[index].id == id) {
                _warmUpSets.value.removeAt(index)
                break
            }
        }
    }

    fun updateWarmUpSetWeight(id: Long, newWeight: Double) {
        _warmUpSets.value.forEach {
            if (it.id == id) {
                it.exerciseWeight =
                    newWeight.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }
    }

    fun updateWarmUpSetReps(id: Long, newReps: Int) {
        _warmUpSets.value.forEach { if (it.id == id) it.repetitions = newReps }
    }
}

private fun initWarmUpSets(): MutableList<SetModel> {
    return MutableList(3) { index ->
        SetModel(
            setNumber = index,
            setType = SetTypeModel(
                newID(),
                "Warm Up Set",
                description = /*TODO*/"",
                textColor = Color(255, 178, 0) /* Color: Orange */
            ),
            exerciseWeight = 100.00,
            repetitions = 10,
            percentage1RM = 1.0
        )
    }
}
