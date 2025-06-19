@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.features.workout.feature.workoutDialogs.AddSetText
import com.example.myapplication.features.workout.feature.workoutDialogs.SwipeToDismissBoxListItem
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun WarmUpSetItem(
  warmUpSet: Set,
  index: Int,
  percentageText: String,
  repetitionsText: String,
  rirText: String,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
  onPercentageChanged: (uuid: Uuid, percentage: Double) -> Unit,
  onRepetitionsChanged: (uuid: Uuid, reps: Int) -> Unit,
  onRirChanged: (uuid: Uuid, rir: Double?) -> Unit,
  removeSet: (uuid: Uuid) -> Unit
) {
  SwipeToDismissBoxListItem(
    removeSet = { removeSet(warmUpSet.uuid) },
    SetLayout = {
      WarmUpSetLayout(
        warmUpSet = warmUpSet,
        setIndex = index,
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        rirText = rirText,
        onCheckedChange = onCheckedChange,
        onPercentageChanged = onPercentageChanged,
        onRepetitionsChanged = onRepetitionsChanged,
        onRirChanged = onRirChanged
      )
    },
  )
}

@OptIn(ExperimentalMaterialApi::class)
// @Preview
@Composable
fun WarmUpSetLazyColumn(
//  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  sets: List<Set>,
  addSet: () -> Unit,
  warmUpSetItem: @Composable (warmUpSet: Set, index: Int, percentageText: String, repetitionsText: String, rirText: String) -> Unit,
  actionRow: @Composable () -> Unit,
) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

//  val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

  LazyColumn(
    state = listState,
    modifier = Modifier
      .fillMaxWidth(),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = sets.size, key = { index -> sets[index].lazyListItemID }) { index ->
      val warmUpSet = sets[index]

//      val percentageText by remember {
//        mutableStateOf(
//          getFormattedPercentageText(
//            warmUpSet.targetPercentage1RM ?: 1.0
//          )
//        )
//      }
//      val repetitionsText by remember { mutableStateOf(warmUpSet.targetRepetitions?.toString() ?: "") }
//      val rirText by remember { mutableStateOf(warmUpSet.targetRepetitionsInReserve?.toString() ?: "") }

      warmUpSetItem(
        warmUpSet,
        index,
        getFormattedPercentageText(warmUpSet.targetPercentage1RM ?: 1.0),
        warmUpSet.targetRepetitions?.toString() ?: "",
        warmUpSet.targetRepetitionsInReserve?.toString() ?: ""
      )
      /*      WarmUpSetItem(
              warmUpSet = warmUpSet,
              index = index,
              onCheckedChange = onCheckedChange,
              onPercentageChanged = warmUpSetsViewModel::updateWarmUpSetPercentage,
              onRepetitionsChanged = warmUpSetsViewModel::updateWarmUpSetReps,
              onRirChanged = warmUpSetsViewModel::updateWarmUpSetRIR,
              removeSet = warmUpSetsViewModel::remove
            )*/
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
          onClick = {
            addSet()
            coroutineScope.launch {
              // Animate scroll to the last item
              listState.animateScrollToItem(index = sets.size.dec())
            }
          }
        ) {
          AddSetText()
        }
      }
    }

    item { actionRow() }
  }
}
