package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.newID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode
import java.util.*

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
                    id = newID(),
                    uuid = UUID.randomUUID()
                )
            )
        } else {
            _warmUpSets.value.add(
                SetModel()
            )
        }
    }

    fun remove(uuid: UUID) {
        for (index in _warmUpSets.value.indices) {
            if (_warmUpSets.value[index].uuid == uuid) {
                _warmUpSets.value.removeAt(index)
                break
            }
        }
    }

    fun updateWarmUpSetWeight(uuid: UUID, newWeight: Double) {
        _warmUpSets.value.forEach {
            if (it.uuid == uuid) {
                it.exerciseWeight =
                    newWeight.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }
    }

    fun updateWarmUpSetReps(uuid: UUID, newReps: Int) {
        _warmUpSets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
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
