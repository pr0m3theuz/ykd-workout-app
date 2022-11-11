package com.example.myapplication.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.myapplication.newID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

data class SetModel(
    val id: Long = LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 1000)),
    val setNumber: Int? = null,
    var setType: SetTypeModel = SetTypeModel(
        newID(),
        "Normal",
        description = "Normal",
        textColor = Color.White
    ),
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var percentage1RM: Float? = null,
    var setCompleted: Boolean = false
)

// TODO Remove
@RequiresApi(Build.VERSION_CODES.O)
val formatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

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
