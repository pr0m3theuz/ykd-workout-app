package com.example.myapplication.features.workout.feature.workoutDialogs.workingSets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.features.workout.feature.workoutDialogs.AddSetText
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterialApi::class,
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)
// @Preview
@Composable
fun WorkingSetLazyColumn(
  //  workingSetViewModel: WorkingSetViewModel = viewModel(),
  sets: List<Set>,
  //  removeSet: (workingSet: Set) -> Unit,
  addset: () -> Unit,
  workingSetItem:
  @Composable
    (
    workingSet: Set,
    index: Int,
    percentageText: String,
    repetitionsText: String,
    rirText: String,
    rpeText: String,
  ) -> Unit,
  actionRow: @Composable () -> Unit,
) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  requireNotNull(sets) { "workingSets is null" }

  LazyColumn(
    state = listState,
    modifier = Modifier.fillMaxWidth(),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = sets.size, key = { index -> sets[index].lazyListItemID }) { index ->
      val workingSet = sets[index]

      val percentageText = getFormattedPercentageText(workingSet.targetPercentage1RM ?: 1.0)
      val repetitionsText = workingSet.targetRepetitions?.toString() ?: ""
      val rirText = workingSet.targetRepetitionsInReserve?.toString() ?: ""
      val rpeText = workingSet.targetRatePerceivedEffort?.toString() ?: ""

      workingSetItem(workingSet, index, percentageText, repetitionsText, rirText, rpeText)
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
          onClick = {
            addset()
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
