@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features

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
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.features.workout.feature.workoutDialogs.AddSetText
import com.example.myapplication.features.workout.feature.workoutDialogs.SwipeToDismissBoxListItem
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterialApi::class)
// @Preview
@Composable
fun WarmUpSetLazyColumn(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
//  selectedSets: Map<Set, Boolean>,
) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

  LazyColumn(
    state = listState,
    modifier = Modifier
      .fillMaxWidth(),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = warmUpSets.size, key = { index -> warmUpSets[index].lazyListItemID }) { index ->
      val warmUpSet = warmUpSets[index]

      SwipeToDismissBoxListItem(
        removeSet = { warmUpSetsViewModel.remove(warmUpSet.uuid) },
        SetLayout = {
          WarmUpSetLayout(
            setIndex = index,
            warmUpSet = warmUpSet,
//            checked = selectedSets[warmUpSet] ?: false,
            onCheckedChange = onCheckedChange
          )
        },
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
