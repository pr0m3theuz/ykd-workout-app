package com.example.myapplication.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.AddSetText
import com.example.myapplication.components.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
// @Preview
@Composable
fun WarmUpSetLazyColumn(
    warmUpSetsViewModel: WarmUpSetsViewModel = viewModel(),
) {
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()

    val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().height(235.dp)
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = warmUpSets.size, key = { index -> warmUpSets[index].id }) { index ->

            val warmUpSet = warmUpSets[index]

//            var deletedState by remember { mutableStateOf(false) }
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    true
                }
            )

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                warmUpSetsViewModel.remove(warmUpSet.uuid)
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
                    WarmUpSetLayout(
                        setIndex = index,
                        warmUpSet = warmUpSet,
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
        TextButton(
            onClick = {
                warmUpSetsViewModel.addSet()
                coroutineScope.launch {
                    // Animate scroll to the last item
                    listState.animateScrollToItem(index = warmUpSets.size.dec())
                }
            }
        ) {
            AddSetText()
        }
    }
}
