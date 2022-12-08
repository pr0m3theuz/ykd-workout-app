package com.example.myapplication.components.exercise

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel

fun LazyListScope.ExerciseSetsItem(
    exerciseSets: ExerciseSetModel,
    sets: List<SetModel>,
    removeSet: (id: Long) -> Unit
/*    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,

    updateSetType: (id: Long, setType: SetTypeModel) -> Unit,*/

) {
    item {
        ExerciseScreen(
            _exerciseSets = exerciseSets
            /*addSet = { exerciseSets -> exerciseSets.addSet() },
            startRestTimer = { setID -> setID.toULong() }*/
        )
    }

    items(exerciseSets.sets.toList(), key = { item -> item.id }) {
    }

    itemsIndexed(exerciseSets.sets) { index: Int, set: SetModel ->
//        SetItem(
//            index = index,
//            set = set,
//            removeSet = { _ -> set.id },
//            startRestTimer = { _ -> set.id }
//        )
    }

    item { AddSet(addSet = { /* TODO */ }) }
}
