package com.example.myapplication.data

import androidx.compose.ui.graphics.Color
import com.example.myapplication.newID

data class SetModel(
    var id: Long = newID(),
    var setNumber: Int? = null,
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var percentage1RM: Float? = 1.0f,
    var setCompleted: Boolean = false,
    var setType: SetTypeModel = SetTypeModel(
        newID(),
        "Normal",
        description = "Normal",
        textColor = Color.White
    )
)

class Set(_setModel: SetModel) {

    val setModel: SetModel = _setModel

    fun updateSetWeight(newWeight: Double?) {
        setModel.exerciseWeight = newWeight
    }

    fun updateSetReps(id: Long, newReps: Int?) {
        setModel.repetitions = newReps
    }

    fun updateSetRIR(newRIR: Double?) {
        setModel.repetitionsInReserve = newRIR
    }

    fun updateSetRPE(newRPE: Double?) {
        setModel.ratePerceivedEffort = newRPE
    }

    fun setComplete(setComplete: Boolean) {
        setModel.setCompleted = setComplete
    }
}

fun SetModel.setVolume(): Double? {
//    Log.i("SetModel.setVolume()", "${exerciseWeight?.times(repetitions!!)}")

    return exerciseWeight?.times(repetitions!!)
}
