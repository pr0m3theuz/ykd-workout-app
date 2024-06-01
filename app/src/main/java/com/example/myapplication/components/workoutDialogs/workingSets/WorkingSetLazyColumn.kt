package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.AddSetText
import com.example.myapplication.components.workoutDialogs.SwipeToDismissBoxListItem
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterialApi::class,
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)
// @Preview
@Composable
fun WorkingSetLazyColumn(
  workingSetViewModel: WorkingSetViewModel = viewModel(),
  //  _workingSets: MutableList<Set>,
  //  removeSet: (workingSet: Set) -> Unit,
  addset: () -> Unit,
) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  val workingSets by workingSetViewModel.sets.collectAsState()

  LazyColumn(
    state = listState,
    modifier = Modifier.fillMaxWidth().height(235.dp),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = workingSets.size, key = { index -> workingSets[index].lazyListItemID }) { index ->
      val workingSet = workingSets[index]

      SwipeToDismissBoxListItem(
        removeSet = { workingSetViewModel.remove(workingSet) },
        SetLayout = { WorkingSetLayout(setIndex = index, workingSet = workingSet) },
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
        addset()
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
