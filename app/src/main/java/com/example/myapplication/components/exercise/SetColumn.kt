package com.example.myapplication.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun SetsScreen(
//    workoutViewModel: WorkoutViewModel = viewModel(),
    _sets: List<SetModel>,
    removeSet: (setID: Long) -> Unit,
    _setComplete: (id: Long, setComplete: Boolean) -> Unit,
    startRestTimer: (setID: Long) -> Unit
) {
//    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()
    val sets = remember { mutableStateOf(listOf<SetModel>()) }
    sets.value = _sets
    Log.i("SetsScreen()", "$sets")

    SetColumn(
        sets.value,
        { setID: Long -> removeSet(setID) },
/*                { *//*  setID: Long, newWeight: Double? -> updateSetWeight(setID, newWeight)  *//* },
                { *//*  setID: Long, newReps: Int? -> updateSetReps(setID, newReps)  *//* },
                { *//*  setID: Long, newRIR: Double? -> updateSetWeight(setID, newRIR)  *//* },
                { *//*  setID: Long, newRPE: Double? -> updateSetWeight(setID, newRPE)  *//* },

                { *//*  setID: Long, setType: SetTypeModel -> updateSetType(setID, setType)  *//* },*/

        { setID: Long, setComplete: Boolean ->
            _setComplete(
                setID,
                setComplete
            )
        },
        { setID: Long -> startRestTimer(setID) }
    )
}

// TODO Understand SwipeToDismiss Behaviour Better and
// TODO Revisit var vs val usage
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
// @Preview
@Composable
fun SetColumn(
    sets: List<SetModel>,
    removeSet: (id: Long) -> Unit,
/*    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,

    updateSetType: (id: Long, setType: SetTypeModel) -> Unit,*/
    setComplete: (id: Long, setComplete: Boolean) -> Unit,
    startRestTimer: (id: Long) -> Unit
) {
//    val sets = _sets
    Log.i("SetColumn()", "$sets")

//    LazyColumn(userScrollEnabled = false, modifier = Modifier.height(300.dp)) {
//    itemsIndexed(sets, key = {_, item -> item.id }) { index, set ->
    sets.forEachIndexed { index, set ->
        val coroutineScope = rememberCoroutineScope()
        var setCompletedState by remember { mutableStateOf(false) }
        val openDialog = remember { mutableStateOf(true) }
//            var deletedState by remember { mutableStateOf(false) }

        val dismissState = rememberDismissState(
            confirmStateChange = {
                true
            }
        )

/*        fun newWeight(newWeight: Double?) {
            updateSetWeight(set.id, newWeight)
        }

        fun newReps(newReps: Int?) {
            updateSetReps(set.id, newReps)
        }

        fun newRIR(newRIR: Double?) {
            updateSetRIR(set.id, newRIR)
        }

        fun newRPE(newRPE: Double?) {
            updateSetRPE(set.id, newRPE)
        }

        fun newSetType(setType: SetTypeModel) {
            updateSetType(set.id, setType)
        }*/

        fun deleteCompletedSet(delete: Boolean) {
//                deletedState = delete
            if (delete) {
//                    coroutineScope.launch { dismissState.animateTo(targetValue = DismissValue.DismissedToStart, anim = SnapSpec(0)) }
                removeSet(set.id)
            } else {
                coroutineScope.launch {
                    dismissState.animateTo(
                        targetValue = DismissValue.Default,
                        anim = TweenSpec()
                    )
                }
            }
        }

        fun changesetCompletedState(status: Boolean) {
            setCompletedState = status
            setComplete(set.id, status)
        }

        if (setCompletedState) startRestTimer(set.id)

        fun openConfirmCompletedSetDeletionDialog(open: Boolean) {
            openDialog.value = open
        }

        if (!setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
            removeSet(set.id)
        } else if (setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
            ConfirmCompletedSetDeletionDialog(
                { open -> openConfirmCompletedSetDeletionDialog(open) },
                { delete -> deleteCompletedSet(delete) }
            )
        }

        SwipeToDismiss(
            state = dismissState,
            // modifier = Modifier.padding(vertical = 4.dp),
            directions = setOf(DismissDirection.EndToStart)
            /*    (if (!exerciseSet.setCompleted) DismissDirection.EndToStart else null)
            ) as Set<DismissDirection>*/,
            dismissThresholds = { dismissDirection ->
                FractionalThreshold(
                    if (dismissDirection == DismissDirection.EndToStart) 0.5f else 1f
                )
            },
            background = {
                // val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        DismissValue.Default -> MaterialTheme.colorScheme.primary
                        DismissValue.DismissedToStart -> Color.Red
                        DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
                    }
                )
                val alignment = Alignment.CenterEnd
                val icon = Icons.Filled.Delete
                val scale by animateFloatAsState(
                    if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f
                )

                Box(
                    Modifier.fillMaxSize().background(color).padding(horizontal = 12.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        tint = White,
                        contentDescription = "Localized description",
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        ) {
            Card(
                backgroundColor = if (setCompletedState) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(CornerSize(0f))
            ) {
                SetLayout(
                    index,
                    set,
                    { completed -> changesetCompletedState(completed) },
                    { /*  newWeight -> newWeight(newWeight)  */ },
                    { /*  newReps -> newReps(newReps)  */ },
                    { /*  newRIR -> newRIR(newRIR)  */ },
                    { /*  newRPE -> newRPE(newRPE)  */ },
                    { /*  setType -> newSetType(setType)  */ }
//                        iconText = if (exerciseSet.setType.name == "Normal") {
//                            index.inc().toString()
//                        } else {
//                            exerciseSet.setType.name[0].toString()
//                        },
//                        iconColor = exerciseSet.setType.textColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetItem(
    index: Int,
    set: SetModel,
    removeSet: () -> Unit,
/*    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,

    updateSetType: (id: Long, setType: SetTypeModel) -> Unit,*/
    setComplete: (setComplete: Boolean) -> Unit,
    startRestTimer: () -> Unit
) {
//    Log.i("SetColumn()", "$set")

    val coroutineScope = rememberCoroutineScope()
    var setCompletedState by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(true) }
//            var deletedState by remember { mutableStateOf(false) }

    val dismissState = rememberDismissState(
        confirmStateChange = {
            true
        }
    )

/*        fun newWeight(newWeight: Double?) {
            updateSetWeight(set.id, newWeight)
        }

        fun newReps(newReps: Int?) {
            updateSetReps(set.id, newReps)
        }

        fun newRIR(newRIR: Double?) {
            updateSetRIR(set.id, newRIR)
        }

        fun newRPE(newRPE: Double?) {
            updateSetRPE(set.id, newRPE)
        }

        fun newSetType(setType: SetTypeModel) {
            updateSetType(set.id, setType)
        }*/

    fun deleteCompletedSet(delete: Boolean) {
//                deletedState = delete
        if (delete) {
//                    coroutineScope.launch { dismissState.animateTo(targetValue = DismissValue.DismissedToStart, anim = SnapSpec(0)) }
            removeSet()
        } else {
            coroutineScope.launch {
                dismissState.animateTo(
                    targetValue = DismissValue.Default,
                    anim = TweenSpec()
                )
            }
        }
    }

    fun changesetCompletedState(status: Boolean) {
        setCompletedState = status
        setComplete(status)
    }

    if (setCompletedState) startRestTimer()

    fun openConfirmCompletedSetDeletionDialog(open: Boolean) {
        openDialog.value = open
    }

    if (!setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
        removeSet()
    } else if (setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
        ConfirmCompletedSetDeletionDialog(
            { open -> openConfirmCompletedSetDeletionDialog(open) },
            { delete -> deleteCompletedSet(delete) }
        )
    }

    SwipeToDismiss(
        state = dismissState,
        // modifier = Modifier.padding(vertical = 4.dp),
        directions = setOf(DismissDirection.EndToStart)
        /*    (if (!exerciseSet.setCompleted) DismissDirection.EndToStart else null)
        ) as Set<DismissDirection>*/,
        dismissThresholds = { dismissDirection ->
            FractionalThreshold(
                if (dismissDirection == DismissDirection.EndToStart) 0.5f else 1f
            )
        },
        background = {
            // val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colorScheme.primary
                    DismissValue.DismissedToStart -> Color.Red
                    DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
                }
            )
            val alignment = Alignment.CenterEnd
            val icon = Icons.Filled.Delete
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f
            )

            Box(
                Modifier.fillMaxSize().background(color).padding(horizontal = 12.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    tint = White,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        Card(
            backgroundColor = if (setCompletedState) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(CornerSize(0f))
        ) {
            SetLayout(
                index,
                set,
                { completed -> changesetCompletedState(completed) },
                { /*  newWeight -> newWeight(newWeight)  */ },
                { /*  newReps -> newReps(newReps)  */ },
                { /*  newRIR -> newRIR(newRIR)  */ },
                { /*  newRPE -> newRPE(newRPE)  */ },
                { /*  setType -> newSetType(setType)  */ }
//                        iconText = if (exerciseSet.setType.name == "Normal") {
//                            index.inc().toString()
//                        } else {
//                            exerciseSet.setType.name[0].toString()
//                        },
//                        iconColor = exerciseSet.setType.textColor
            )
        }
    }
}
