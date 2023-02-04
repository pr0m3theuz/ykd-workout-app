package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.newID
import kotlin.math.roundToInt

data class ExerciseSetModel(
    val id: Long = newID(),
    val exercise: ExerciseModel,
    var sets: MutableList<SetModel>
)

fun ExerciseSetModel.addSet() {
    if (sets.isNotEmpty()) {
        sets.add(
            sets.last().copy(
                id = newID(),
                setNumber = sets.last().setNumber?.plus(1),
                setCompleted = false
            )
        )
    } else {
        sets.add(
            SetModel()
        )
    }

    Log.i("workoutViewModel.addSet()", "${sets.size}")
}

fun ExerciseSetModel.removeSet(id: Long) {
    sets.removeIf { it.id == id }
}

fun ExerciseSetModel.updateSetWeight(id: Long, newWeight: Double?) {
    sets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
}

fun ExerciseSetModel.updateSetReps(id: Long, newReps: Int?) {
    sets.forEach { if (it.id == id) it.repetitions = newReps }
}

fun ExerciseSetModel.updateSetRIR(id: Long, newRIR: Double?) {
    sets.forEach { if (it.id == id) it.ratePerceivedEffort = newRIR }
}

fun ExerciseSetModel.updateSetRPE(id: Long, newRPE: Double?) {
    sets.forEach { if (it.id == id) it.ratePerceivedEffort = newRPE }
}

fun ExerciseSetModel.setComplete(id: Long, setComplete: Boolean) {
    sets.forEach { if (it.id == id) it.setCompleted = setComplete }
}

fun ExerciseSetModel.updateSetType(id: Long, setType: SetTypeModel) {
    sets.forEach { if (it.id == id) it.setType = setType }
}

fun ExerciseSetModel.addWarmUpSets(warmUpSets: List<SetModel>) {
    for (index in warmUpSets.indices) {
        sets.add(index, warmUpSets[index].copy(id = newID()))
    }
}

fun ExerciseSetModel.getWorkingSetWeight(): Double? {
//        for (index in sets.indices) {
//            if (sets[index].setType.name != "Warm Up Set") {
//                return sets[index].exerciseWeight
//                break
//            }
//        }
//        return null
    // TODO Add If Not Empty
    return sets.first { it.setType.name != "Warm Up Set" }.exerciseWeight
}

fun ExerciseSetModel.addWorkingSets(workingSets: List<SetModel>) {
//        for (index in sets.indices) {
//            if (sets[index].setType.name != "Warm Up Set") {
//                sets.removeAt(index = index)
//            }
//        }
    workingSets.forEach { it.id = newID() }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Before: ${sets.toList()}")
    sets.removeAll { it.setType.name != "Warm Up Set" }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Removed Old Sets: ${sets.toList()}")
    sets.addAll(workingSets)
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Added New Sets: ${sets.toList()}")
}

fun ExerciseSetModel.addBackOffSets(backOffSets: List<SetModel>) {
    for (index in backOffSets.indices) {
        sets.add(backOffSets[index].copy(id = newID()))
    }

//        backOffSets.forEach { it.id = newID() }
//        sets.addAll(backOffSets)
}

fun ExerciseSetModel.totalWorkingSetVolume(): Double {
    var totalVolume: Double = 0.0

//    Log.i("ExerciseSetModel sets", "$sets")

    sets.filter { it.setType.name != "Warm Up Set" }.forEach {
        totalVolume += (it.setVolume()!!)
//        Log.i("ExerciseSetModel exerciseWeight", "${it.exerciseWeight}")
//        Log.i("ExerciseSetModel repetitions", "${it.repetitions}")
//        Log.i("ExerciseSetModel setVolume()", "${it.setVolume()}")
//        Log.i("ExerciseSetModel.totalWorkingSetVolume()", "$totalVolume")
    }

    return totalVolume
}

fun ExerciseSetModel.numberOfWorkingSets(): Int {
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setType.name != "Warm Up Set" }.forEach {
        numberOfWorkingSets++
//        Log.i("ExerciseSetModel setVolume()", "${it.setVolume()}")
//        Log.i("ExerciseSetModel.totalWorkingSetVolume()", "$totalVolume")
    }

    return numberOfWorkingSets
}

//    Returns the number of reps to be performed based on the new weight
fun ExerciseSetModel.equateWorkingSetVolumeByWeight(weight: Double?): Int {
    var totalVolume: Double = 0.0
    var numberOfWorkingSets: Int = 0
    var newRepetitions: Int = 0

    sets.filter { it.setType.name != "Warm Up Set" }.forEach {
        totalVolume += (it.setVolume()!!)
        numberOfWorkingSets++
    }

    newRepetitions = if (numberOfWorkingSets > 0 && weight != null && weight > 0.0) {
        totalVolume.div(weight).div(numberOfWorkingSets).roundToInt()
    } else {
        0
    }

//    Log.i("ExerciseSetModel.equateWorkingSetVolumeByWeight() newReps", "$newRepetitions")
    return newRepetitions
}

//    Returns the weight to be used based on the new repetitions
fun ExerciseSetModel.equateWorkingSetVolumeByRepetitions(repetitions: Int?): Double {
    var totalVolume: Double = 0.0
    var numberOfWorkingSets: Int = 0
    var newWeight: Double = 0.0

    sets.filter { it.setType.name != "Warm Up Set" }.forEach {
        totalVolume += (it.setVolume())
        numberOfWorkingSets++
    }

    newWeight = if (numberOfWorkingSets > 0 && repetitions != null && repetitions > 0) {
        totalVolume.div(repetitions).div(numberOfWorkingSets)
    } else {
        0.0
    }
//    Log.i("ExerciseSetModel.equateWorkingSetVolumeByWeight() newWeight", "$newWeight")
    return newWeight
}

fun ExerciseSetModel.totalVolume(): Double {
    var totalVolume = 0.0

    sets.forEach { totalVolume += it.setVolume() }

//    Log.i("totalVolume():", "$totalVolume")

    return totalVolume
}

fun ExerciseSetModel.totalRepetitions(): Int {
    var totalRepetitions = 0

    sets.forEach {
        (if (it.repetitions != null) it.repetitions else 0)?.let { it1 ->
            totalRepetitions += (it1)
        }
    }

//    Log.i("totalRepetitions():", "$totalRepetitions")

    return totalRepetitions
}

fun ExerciseSetModel.averageWeight(): Double {
    var averageWeight = 0.0

    averageWeight = totalVolume() / totalRepetitions()

//    Log.i("averageWeight():", "$averageWeight")

    return if (averageWeight.isNaN()) {
        0.0
    } else {
        averageWeight
    }
}
