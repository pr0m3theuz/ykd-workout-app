package com.example.myapplication.components.exercise

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.ConfirmCompletedSetDeletionDialog
import com.example.myapplication.components.SetLayout
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetItem(
    exerciseSets: ExerciseSetModel,
    index: Int,
    set: SetModel,
    removeSet: () -> Unit,
    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,
    updateSetType: (id: Long, setType: SetTypeModel) -> Unit,
    setComplete: (setComplete: Boolean) -> Unit // ,
//    startRestTimer: () -> Unit
) {
//    Log.i("SetColumn()", "$set")

    val coroutineScope = rememberCoroutineScope()
    var setCompletedState by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(true) }

    val dismissState = rememberDismissState(
        confirmStateChange = {
            true
        }
    )

    fun newWeight(newWeight: Double?) {
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
    }

    fun deleteCompletedSet(delete: Boolean) {
        if (delete) {
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

//    if (setCompletedState) startRestTimer()

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
                exerciseSets = exerciseSets,
                index,
                set,
                { completed -> changesetCompletedState(completed) },
                { newWeight -> newWeight(newWeight) },
                { newReps -> newReps(newReps) },
                { newRIR -> newRIR(newRIR) },
                { newRPE -> newRPE(newRPE) },
                { setType -> newSetType(setType) }
            )
        }
    }
}
