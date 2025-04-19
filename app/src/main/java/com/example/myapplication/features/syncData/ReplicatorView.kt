package com.example.myapplication.features.syncData

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.common.ui.component.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplicatorView(
  navController: NavController,
  viewModel: ReplicatorViewModel,
//  openDrawer: () -> Unit,
  replicatorConfigNav: () -> Unit,
//  scaffoldState: ScaffoldState = rememberScaffoldState()
) {

  val replicationStatus by viewModel.replicationStatus.collectAsState()
  val replicationProgress by viewModel.replicationProgress
  val logMessage = viewModel.logMessages
  val isButtonShown by viewModel.isButtonActive.collectAsState()
//  Log.i("ReplicatorView.viewModel.replicatorService",viewModel.)
  // A surface container using the 'background' color from the theme

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row {
            Text(text = "Replicator Status")
          }
        },
        actions = {
          IconButton(onClick = { replicatorConfigNav() }) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = "",
              modifier = Modifier
                .padding(end = 10.dp)
            )
          }
        }
      )
    },
    bottomBar = { BottomNavigationBar(navController) })
  { contentPadding ->
    Surface(
//        color = MaterialTheme.colorScheme.background,
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
    )
    {
      ReplicatorWidget(
        replicationStatus = replicationStatus,
        replicationProgress = replicationProgress,
        logMessage = logMessage,
        isButtonShown = isButtonShown,
        onStartClick = viewModel::onStartClick,
        onStopClick = viewModel::onStopClick,
        onClearLogs = viewModel::clearLogs
      )
    }
  }


}

@Composable
fun ReplicatorWidget(
  replicationStatus: String,
  replicationProgress: String,
  logMessage: List<String>,
  isButtonShown: Boolean,
  onStartClick: () -> Unit,
  onStopClick: () -> Unit,
  onClearLogs: () -> Unit
) {
  Log.i("ReplicatorWidget: isButtonShown", isButtonShown.toString())
  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(16.dp)
  ) {
    item {
      Text(text = "Replicator Status: $replicationStatus")
    }
    item {
      Text(
        text = "Replicator Progress: $replicationProgress",
        modifier = Modifier.padding(top = 10.dp)
      )
    }
    item {
      ReplicatorDivider()
    }
    if (isButtonShown) {
      item {
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
        ) {
          Button(modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
            onClick = {
              onStartClick()
            })
          {
            Text(
              "Start",
              style = MaterialTheme.typography.titleSmall
            )
          }
          Button(modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
            onClick = {
              onStopClick()
            })
          {
            Text(
              "Stop",

              style = MaterialTheme.typography.titleSmall
            )
          }
          Button(modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
            onClick = { onClearLogs() }
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
//              modifier = Modifier.size(24.dp, 24.dp)
            )
          }
        }
      }
      item {
        ReplicatorDivider()
      }
//      logMessage.forEach {
//        item {
//          Text(it)
//        }
//      }
    }
  }
}

@Composable
fun ReplicatorDivider() {
  HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
}

//@OptIn(InternalCoroutinesApi::class)
//@Preview(showBackground = true)
//@Composable
//fun ReplicatorViewPreview(){
//    val viewModel = ReplicatorViewModel(ReplicatorServiceMock())
//    MaterialTheme {
//        ReplicatorWidget(
//            replicationStatus = viewModel.replicationStatus.value,
//            replicationProgress = viewModel.replicationProgress.value,
//            logMessage =  viewModel.logMessages,
//            isButtonShown = true,
//            onStartClick = viewModel::onStartClick,
//            onStopClick = viewModel::onStopClick,
//            onClearLogs = viewModel::clearLogs
//        )
//    }
//}