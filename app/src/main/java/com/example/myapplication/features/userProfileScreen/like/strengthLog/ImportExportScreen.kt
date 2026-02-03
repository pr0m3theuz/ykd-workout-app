@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toFile
import com.example.myapplication.MainActivity
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

  val pickImportFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
    if (result != null) {
      onImport(result)
    }
  }

  val exportFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { result ->
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
        Button(onClick = { launchImport() }) {
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