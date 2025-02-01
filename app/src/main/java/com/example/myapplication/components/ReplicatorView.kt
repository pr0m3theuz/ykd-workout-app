package com.example.myapplication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ReplicatorView(
  navController: NavController,
  viewModel: ReplicatorViewModel,
//  openDrawer: () -> Unit,
//  replicatorConfigNav: () -> Unit,
//  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  val scaffoldState: ScaffoldState = rememberScaffoldState()

  MaterialTheme {
    // A surface container using the 'background' color from the theme
    Scaffold(scaffoldState = scaffoldState,
      topBar = {
        TopAppBar(title = {
          Row {
            Text(text = "Replicator Status")
          }
        },
          navigationIcon = {
            IconButton(
//              modifier = Modifier.semantics { contentDescription = menuResource },
              onClick = { /* TODO openDrawer()*/ }) {
              Icon(Icons.Filled.Menu, contentDescription = "")
            }
          },
          actions = {
            IconButton(onClick = {/* TODO replicatorConfigNav()*/ }) {
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
        color = MaterialTheme.colors.background,
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
      )
      {
        ReplicatorWidget(
          replicationStatus = viewModel.replicationStatus.value,
          replicationProgress = viewModel.replicationProgress.value,
          logMessage = viewModel.logMessages,
          isButtonShown = true, // viewModel.isButtonActive.value,
          onStartClick = viewModel::onStartClick,
          onStopClick = viewModel::onStopClick,
          onClearLogs = viewModel::clearLogs
        )
      }
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
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            onClick = {
              onStartClick()
            })
          {
            Text(
              "Start",
              color = Color.White,
              style = MaterialTheme.typography.h5
            )
          }
          Button(modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            onClick = {
              onStopClick()
            })
          {
            Text(
              "Stop",
              color = Color.White,
              style = MaterialTheme.typography.h5
            )
          }
          Button(modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            onClick = { onClearLogs() }
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              modifier = Modifier
                .size(40.dp, 30.dp),
              tint = Color.White
            )
          }
        }
      }
      item {
        ReplicatorDivider()
      }
      logMessage.forEach {
        item {
          Text(it)
        }
      }
    }
  }
}

@Composable
fun ReplicatorDivider() {
  Divider(
    color = Color.DarkGray,
    thickness = 1.dp,
    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
  )
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