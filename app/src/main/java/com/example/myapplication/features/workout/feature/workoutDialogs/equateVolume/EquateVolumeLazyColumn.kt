package com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.features.workout.feature.workoutDialogs.AddSetText
import com.example.myapplication.features.workout.feature.workoutDialogs.SwipeToDismissBoxListItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
// @Preview
@Composable
fun EquateVolumeLazyColumn(equateVolumeViewModel: EquateVolumeViewModel = koinViewModel()) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  val workingSets by equateVolumeViewModel.sets.collectAsState()

  LazyColumn(
    state = listState,
    modifier = Modifier
      .fillMaxWidth(),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = workingSets.size, key = { index -> workingSets[index].lazyListItemID }) { index ->
      val workingSet = workingSets[index]

      SwipeToDismissBoxListItem(
        removeSet = { equateVolumeViewModel.removeSet(workingSet.lazyListItemID) },
        SetLayout = { EquateVolumeLayout(setIndex = index, workingSet = workingSet) },
      )
    }
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TextButton(
      onClick = {
        equateVolumeViewModel.addSet()
        coroutineScope.launch {
          // Animate scroll to the last item
          listState.animateScrollToItem(index = workingSets.size.dec())
        }
      }
    ) {
      AddSetText()
    }
  }
}
