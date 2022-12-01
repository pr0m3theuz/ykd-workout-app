package com.example.myapplication.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

// TODO Understand SwipeToDismiss Behaviour Better and
// TODO Revisit var vs val usage
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
// @Preview
@Composable
fun SetLazyColumn(
    exerciseSets: List<SetModel>,
    deleteSet: (id: Long) -> Unit,
    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,
    setComplete: (id: Long, setComplete: Boolean) -> Unit,
    updateSetType: (id: Long, setType: SetTypeModel) -> Unit,
    startRestTimer: (id: Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = exerciseSets.size, key = { index -> exerciseSets[index].id }) { index ->
            val coroutineScope = rememberCoroutineScope()
            val exerciseSet = exerciseSets[index]
            var setCompletedState by remember { mutableStateOf(false) }
            val openDialog = remember { mutableStateOf(true) }
//            var deletedState by remember { mutableStateOf(false) }

            val dismissState = rememberDismissState(
                confirmStateChange = {
                    true
                }
            )

            fun newWeight(newWeight: Double?) {
                updateSetWeight(exerciseSet.id, newWeight)
            }

            fun newReps(newReps: Int?) {
                updateSetReps(exerciseSet.id, newReps)
            }

            fun newRIR(newRIR: Double?) {
                updateSetRIR(exerciseSet.id, newRIR)
            }

            fun newRPE(newRPE: Double?) {
                updateSetRPE(exerciseSet.id, newRPE)
            }

            fun newSetType(setType: SetTypeModel) {
                updateSetType(exerciseSet.id, setType)
            }

            fun deleteCompletedSet(delete: Boolean) {
//                deletedState = delete
                if (delete) {
//                    coroutineScope.launch { dismissState.animateTo(targetValue = DismissValue.DismissedToStart, anim = SnapSpec(0)) }
                    deleteSet(exerciseSets[index].id)
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
                setComplete(exerciseSet.id, status)
            }

            if (setCompletedState) startRestTimer(exerciseSet.id)

            fun openConfirmCompletedSetDeletionDialog(open: Boolean) {
                openDialog.value = open
            }

            if (!setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
                deleteSet(exerciseSets[index].id)
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
                    ExerciseSetLayout(
                        index,
                        exerciseSet,
                        { completed -> changesetCompletedState(completed) },
                        { newWeight -> newWeight(newWeight) },
                        { newReps -> newReps(newReps) },
                        { newRIR -> newRIR(newRIR) },
                        { newRPE -> newRPE(newRPE) },
                        { setType -> newSetType(setType) }
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
}
