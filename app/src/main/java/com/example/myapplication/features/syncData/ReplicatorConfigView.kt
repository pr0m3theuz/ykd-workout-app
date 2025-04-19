@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.syncData


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReplicatorConfigView(
  viewModel: ReplicatorViewModel,
  navigateUp: () -> Unit,
) {

  Scaffold(
    topBar = {
      TopAppBar(
        title =
        { Row { Text("Replicator Config") } },
      )
    })
  { padding ->
    Surface(
//      color = MaterialTheme.colorScheme.background,
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      ReplicatorConfigOptions(
        serverUrl = viewModel.serverUrlState.value,
        onServerUrlChanged = viewModel.onServerUrlChanged,
        heartBeat = viewModel.heartBeatState.longValue.toString(),
        onHeartBeatChanged = viewModel.onHeartBeatChanged,
        isContinuous = viewModel.continuousState.value,
        onContinuousChanged = viewModel.onContinuousChanged,
        useSelfSignedCert = viewModel.selfSignedCertState.value,
        onUseSelfSignedCert = viewModel.onSelfSignedCertChanged,
        replicatorTypes = viewModel.replicatorTypes,
        replicatorTypeSelected = viewModel.replicatorTypeSelected.value,
        onReplicatorTypeChanged = viewModel.onReplicatorTypeChanged,
        username = viewModel.username,
        password = viewModel.password,
        onSave = viewModel::save,
        navigateUp = navigateUp
      )
    }
  }

}

@Composable
fun ReplicatorConfigOptions(
  serverUrl: String,
  onServerUrlChanged: (String) -> Unit,
  heartBeat: String,
  onHeartBeatChanged: (Long) -> Unit,
  isContinuous: Boolean,
  onContinuousChanged: (Boolean) -> Unit,
  useSelfSignedCert: Boolean,
  onUseSelfSignedCert: (Boolean) -> Unit,
  replicatorTypes: List<String>,
  replicatorTypeSelected: String,
  onReplicatorTypeChanged: (String) -> Unit,
  username: String,
  password: String,
  onSave: () -> Unit,
  navigateUp: () -> Unit
) {
  val rowPadding = PaddingValues(top = 20.dp, start = 16.dp, end = 16.dp)

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    item {
      Row(
        modifier = Modifier
          .padding(top = 10.dp, start = 16.dp, end = 16.dp)
          .fillMaxWidth()
      ) {
        Text("Configuration", style = MaterialTheme.typography.titleMedium)
      }
    }
    item {
      Row(
        modifier = Modifier
          .padding(rowPadding)
          .fillMaxWidth()
      ) {
        TextField(modifier = Modifier.fillMaxWidth(),
          value = serverUrl,
          onValueChange = {
            onServerUrlChanged(it)
          },
          label = { Text("Sync Gateway Server URL") })
      }
    }
    /*    item {
          Row(
            modifier = Modifier
              .padding(rowPadding)
              .fillMaxWidth()
          )
          {
            TextField(modifier = Modifier.fillMaxWidth(),
              value = heartBeat,
              onValueChange = {
                it.toLongOrNull()?.let { value ->
                  onHeartBeatChanged(value)
                }
              },
              label = { Text("Heartbeat (in Seconds)") }
            )
          }
        }*/
    item {
      Row(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth()
      ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
          OutlinedTextField(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { expanded = true },
            value = replicatorTypeSelected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
              IconButton(onClick = {
                expanded = true
              }) { Icon(Icons.Default.ArrowDropDown, contentDescription = "dropdown menu") }
            },
            label = { Text("Replicator Type") }
          )

          DropdownMenu(
            modifier = Modifier.fillMaxWidth(0.5f),
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            replicatorTypes.forEach { type ->
              DropdownMenuItem(
                text = { Text(type) },
                onClick = {
                  onReplicatorTypeChanged(type)
                  expanded = false
                })
            }
          }
        }
      }


    }
    item {
      Row(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(weight = 1.0f)) {
          Text("Continuous")
          Text(
            "Automatically sync data to and from the server",
            fontSize = MaterialTheme.typography.labelSmall.fontSize
          )
        }
        Switch(
          checked = isContinuous,
          onCheckedChange = {
            onContinuousChanged(it)
          }
        )
      }
    }
    /*    item {
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Accept Only Self-Signed Certs", modifier = Modifier.weight(weight = 1.0f))
            Switch(
              checked = useSelfSignedCert,
              onCheckedChange = {
                onUseSelfSignedCert(it)
              }
            )
          }
        }*/
    item {
      DividerRow()
    }
    item {
      Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        Text("Authentication", style = MaterialTheme.typography.titleLarge)
      }
    }
    item {
      Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
        Text("Username: $username")
      }
    }
    item {
      Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
        Text("Password: $password")
      }
    }
    item {
      DividerRow()
    }
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(onClick = {
          //save and head back
          onSave()
          //navigate back
          navigateUp()
        }) { Text("Save") }
      }

    }
  }
}

@Composable
fun DividerRow() {
  Row(modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)) {
    HorizontalDivider()
  }
}

//@OptIn(InternalCoroutinesApi::class)
//@Preview(showBackground = true)
//@Composable
//fun ReplicationConfigScreenPreview() {
//  val viewModel = ReplicatorConfigViewModel(ReplicatorServiceMock())
//
//    ReplicatorConfigView(viewModel = viewModel, navigateUp = { })
//
//}