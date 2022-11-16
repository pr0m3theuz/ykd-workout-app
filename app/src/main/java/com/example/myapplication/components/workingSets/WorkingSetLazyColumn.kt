package com.example.myapplication.components.workingSets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
// @Preview
@Composable
fun WorkingSetLazyColumn(
    workingSets: List<SetModel>,
    _workingWeight: Double,
    deleteSet: (id: Long) -> Unit,
    updateWorkingWeight: (id: Long, newWeight: Double) -> Unit,
    updateWorkingSetReps: (id: Long, newReps: Int) -> Unit,
    updateWorkingSetPercentage: (id: Long, newPercentage: Float) -> Unit,
    addSet: () -> Unit
) {
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().height(235.dp)
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        /*
        stickyHeader {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .width(48.dp),
                    contentAlignment = Alignment.Center

                ) {
                    androidx.compose.material3.Text("Set")
                }

                Box(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 4.dp)),
                    contentAlignment = Alignment.Center
                ) { androidx.compose.material3.Text("Percent") }

                Box(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 4.dp))
                        .width(50.dp),
                    contentAlignment = Alignment.Center
                ) { androidx.compose.material3.Text("Reps") }

                Box(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 4.dp))
                        .width(60.dp),
                    contentAlignment = Alignment.Center
                ) { androidx.compose.material3.Text("Warm-up") }
            }
        }
        */

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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextButton(
            onClick = {
                addSet()
                coroutineScope.launch {
                    // Animate scroll to the last item
                    listState.animateScrollToItem(index = workingSets.size.dec())
                }
            }
        ) {
            androidx.compose.material3.Text("Add Set")
        }
    }
}
