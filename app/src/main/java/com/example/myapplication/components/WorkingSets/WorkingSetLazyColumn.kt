package com.example.myapplication.components.WorkingSets

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.White

@OptIn(ExperimentalMaterialApi::class)
// @Preview
@Composable
fun WorkingSetLazyColumn(
    workingSets: List<SetModel>,
    _workingWeight: Double,
    deleteSet: (id: Long) -> Unit,
    updateWorkingWeight: (id: Long, newWeight: Double) -> Unit,
    updateWorkingSetReps: (id: Long, newReps: Int) -> Unit,
    updateWorkingSetPercentage: (id: Long, newPercentage: Float) -> Unit
) {
    LazyColumn(

        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = workingSets.size, key = { index -> workingSets[index].id }) { index ->

            val warmUpSet = workingSets[index]

//            var deletedState by remember { mutableStateOf(false) }
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    true
                }
            )

            fun newWeight(newWeight: Double) {
                updateWorkingWeight(warmUpSet.id, newWeight)
            }

            fun newReps(newReps: Int) {
                updateWorkingSetReps(warmUpSet.id, newReps)
            }

            fun newPercentage(newPercentage: Float) {
                updateWorkingSetPercentage(warmUpSet.id, newPercentage)
            }

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                deleteSet(warmUpSet.id)
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
//                        modifier = Modifier.,
//                    elevation = animateDpAsState(
//                        if (dismissState.dismissDirection != null) 8.dp else 0.dp
//                    ).value,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(CornerSize(0f))
                ) {
                    WorkingSetLayout(
                        index,
                        warmUpSet,
                        _workingWeight,
                        { newWeight -> newWeight(newWeight) },
                        { newReps -> newReps(newReps) },
                        { newPercentage -> newPercentage(newPercentage) }
                    )
                }
            }
        }
    }
}
