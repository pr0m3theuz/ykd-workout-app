@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

@Preview
@Composable
fun ImportExportScreen(
  onNavigateBack: () -> Unit = {},
  bottomNavigationBar: @Composable () -> Unit = {},
  onImport: (file: Uri) -> Unit = {},
  onExport: (file: Uri) -> Unit = {},
  backupEvent: SharedFlow<String>? = null,
) {
  val context = LocalContext.current

  LaunchedEffect(backupEvent) {
    backupEvent?.collectLatest { message ->
      Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
  }

  val openDialog = remember { mutableStateOf(false) }

  val pickImportFile =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
      if (result != null) {
        onImport(result)
      }
    }

  val exportFile =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { result ->
      if (result != null) {
        onExport(result)
      }
    }

  fun launchImport() {
    pickImportFile.launch(arrayOf("application/zip"))
  }

  fun launchExport() {
    exportFile.launch("ykd_data_export_${now().toLocalDateTime(TimeZone.currentSystemDefault())}.zip")
  }

  if (openDialog.value) {
    ConfirmImportingDataDialog(
      onDismissRequest = { openDialog.value = false },
      onConfirm = {
        openDialog.value = false
        // backup database before deleting
        launchImport()
        exportFile.launch("ykd_data_backup_${now().toLocalDateTime(TimeZone.currentSystemDefault())}.zip")
      })
  }


  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Import/Export Data") }, // As per screenshot
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        },
      )
    },
    bottomBar = { bottomNavigationBar() },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Button(onClick = { openDialog.value = true }) {
          Row() {
            Icon(imageVector = Icons.Default.Upload, contentDescription = "Import Data")
            Text("Import Data")
          }
        }
        Button(onClick = { launchExport() }) {
          Icon(imageVector = Icons.Default.Download, contentDescription = "Export Data")
          Text("Export Data")
        }
      }

    }
  }
}

@Composable
fun ConfirmImportingDataDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
) {
  BasicAlertDialog(
    onDismissRequest = onDismissRequest,
  ) {
    Surface(
      modifier = Modifier
        .wrapContentWidth()
        .wrapContentHeight(),
      shape = MaterialTheme.shapes.large,
      tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
          Icon(
            Icons.Default.Upload,
            contentDescription = "Import Data"
          )
        }

        Text(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          text = "Import Data",
          style = MaterialTheme.typography.headlineSmall,
        )


        Spacer(modifier = Modifier.height(24.dp))
        Text(
          text =
            "Confirm if you want import a new database. This will overwrite the current database." +
                    "A backup database will be created before importing. Repeat this process to import the backed-up database.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          TextButton(
            onClick = onDismissRequest,
          ) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(24.dp))
          TextButton(
            onClick = onConfirm,
          ) {
            Text("Confirm")
          }
        }
      }
    }
  }
}