package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.ReplicatorActivityLevel
import com.couchbase.lite.ReplicatorChange
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.services.AuthenticationService
import com.example.myapplication.data.services.ReplicationStatus
import com.example.myapplication.data.services.ReplicatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val REPLICATOR_STATUS = "replicator status"

class MainViewModel(
  private val authService: AuthenticationService,
  private val replicatorService: ReplicatorService,
  private val databaseManager: DatabaseManager,
) : ViewModel() {

  val replicationProgress = mutableStateOf("Not Started")
  val replicationStatus = replicatorService.replicationStatus
//  val isButtonActive = replicatorService.canStartReplication

  private var replicatorFlow: Flow<ReplicatorChange>? = null

  val startDatabase: () -> Unit = {
    viewModelScope.launch(Dispatchers.IO) {
      databaseManager.initializeDatabases(/*authService.getCurrentUser()*/)

      when (replicationStatus.value) {
        ReplicationStatus.STOPPED, ReplicationStatus.IDlE, "" -> {

          Log.i(REPLICATOR_STATUS, "INFORMATION:: Starting Replication...")
          replicatorService.startReplication()

          Log.i(REPLICATOR_STATUS, "INFORMATION:: Getting Replication Change Flow...")
          replicatorFlow = replicatorService.getReplicatorChangeFlow()
          replicatorFlow?.let { flowChange ->
            viewModelScope.launch(Dispatchers.Main) {
              Log.i(REPLICATOR_STATUS, "INFORMATION:: Collecting Replicator Changes")
              flowChange.collect { replicatorChange ->
                Log.i(REPLICATOR_STATUS, "INFORMATION:: Collecting Replication Change Flow...")

                when (replicatorChange.status.activityLevel) {
                  ReplicatorActivityLevel.OFFLINE -> {
                    replicationStatus.value = ReplicationStatus.OFFLINE
                    Log.i(REPLICATOR_STATUS, "INFORMATION:: returned Status OFFLINE...")
                  }

                  ReplicatorActivityLevel.IDLE -> {
                    replicationStatus.value = ReplicationStatus.IDlE
                    Log.i(REPLICATOR_STATUS, "INFORMATION:: returned Status IDLE...")
                  }

                  ReplicatorActivityLevel.STOPPED -> {
                    replicationStatus.value = ReplicationStatus.STOPPED
                    Log.i(REPLICATOR_STATUS, "INFORMATION:: returned Status STOPPED...")
                  }

                  ReplicatorActivityLevel.BUSY -> {
                    replicationStatus.value = ReplicationStatus.BUSY
                    Log.i(REPLICATOR_STATUS, "INFORMATION:: returned Status BUSY...")
                  }

                  ReplicatorActivityLevel.CONNECTING -> {
                    replicationStatus.value = ReplicationStatus.CONNECTING
                    Log.i(REPLICATOR_STATUS, "INFORMATION:: returned Status CONNECTING...")
                  }
                }
                replicatorChange.status.error?.let { error ->
                  Log.i(REPLICATOR_STATUS, "ERROR:: ${error.code} - ${error.message}")
                }
                Log.i(REPLICATOR_STATUS, "INFORMATION:: Checking replication progress...")
                if (replicatorChange.status.progress.completed == replicatorChange.status.progress.total || replicatorChange.status.progress.completed.toInt() == 0) {
                  replicationProgress.value = "Completed"
                  Log.i(REPLICATOR_STATUS, "INFORMATION:: returned STATUS Completed...")
                } else {
                  replicationProgress.value =
                    "${replicatorChange.status.progress.total / replicatorChange.status.progress.completed}"
                }
              }
            }
          }
          Log.i(REPLICATOR_STATUS, "INFORMATION:: Replication Started method completed...")
        }

        else -> {
          Log.i(REPLICATOR_STATUS, "INFORMATION:: Stopping replication...")
          replicatorService.stopReplication()
        }
      }
    }
  }

  val closeDatabase: () -> Unit = {
    viewModelScope.launch(Dispatchers.IO) {
      replicatorService.stopReplication()
      databaseManager.closeDatabases()
    }
  }
}
