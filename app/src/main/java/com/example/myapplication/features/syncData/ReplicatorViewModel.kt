package com.example.myapplication.features.syncData

import android.util.Log
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.ReplicatorActivityLevel
import com.couchbase.lite.ReplicatorChange
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.replicator.ReplicationStatus
import com.example.myapplication.common.infrastructure.replicator.ReplicatorConfig
import com.example.myapplication.common.infrastructure.replicator.ReplicatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReplicatorViewModel(
  private val replicatorService: ReplicatorService,
  private val userProfileManager: UserProfileManager,
) : ViewModel() {

  val logMessages = mutableStateListOf("")
  val replicationProgress = mutableStateOf("Not Started")
  val replicationStatus = replicatorService.replicationStatus
  val isButtonActive = replicatorService.canStartReplication

  private var replicatorFlow: Flow<ReplicatorChange>? = null

  fun clearLogs() {
    logMessages.clear()
  }

  fun onStopClick() {
    viewModelScope.launch(Dispatchers.IO) {
      Log.i("ReplicatorChange", "INFORMATION:: Stopping Replication...")
      replicatorService.stopReplication()
      Log.i("ReplicatorChange", "INFORMATION:: Replication Service notified to stop...")
    }
  }

  fun onStartClick() {
    viewModelScope.launch(Dispatchers.IO) {
      when (replicationStatus.value) {
        ReplicationStatus.STOPPED,
        ReplicationStatus.IDlE,
        "" -> {

          Log.i("ReplicatorChange", "INFORMATION:: Starting Replication...")
          replicatorService.startReplication()

          Log.i("ReplicatorChange", "INFORMATION:: Getting Replication Change Flow...")
          replicatorFlow = replicatorService.getReplicatorChangeFlow()
          replicatorFlow?.let { flowChange ->
            viewModelScope.launch(Dispatchers.Main) {
              Log.i("ReplicatorChange", "INFORMATION:: Collecting Replicator Changes")
              flowChange.collect { replicatorChange ->
                Log.i("ReplicatorChange", "INFORMATION:: Collecting Replication Change Flow...")

                when (replicatorChange.status.activityLevel) {
                  ReplicatorActivityLevel.OFFLINE -> {
                    replicatorService.updateReplicationStatus(ReplicationStatus.OFFLINE)
                    Log.i("ReplicatorChange", "INFORMATION:: returned Status OFFLINE...")
                  }

                  ReplicatorActivityLevel.IDLE -> {
                    replicatorService.updateReplicationStatus(ReplicationStatus.IDlE)
                    Log.i("ReplicatorChange", "INFORMATION:: returned Status IDLE...")
                  }

                  ReplicatorActivityLevel.STOPPED -> {
                    replicatorService.updateReplicationStatus(ReplicationStatus.STOPPED)
                    Log.i("ReplicatorChange", "INFORMATION:: returned Status STOPPED...")
                  }

                  ReplicatorActivityLevel.BUSY -> {
                    replicatorService.updateReplicationStatus(ReplicationStatus.BUSY)
                    Log.i("ReplicatorChange", "INFORMATION:: returned Status BUSY...")
                  }

                  ReplicatorActivityLevel.CONNECTING -> {
                    replicatorService.updateReplicationStatus(ReplicationStatus.CONNECTING)
                    Log.i("ReplicatorChange", "INFORMATION:: returned Status CONNECTING...")
                  }
                }
                replicatorChange.status.error?.let { error ->
                  Log.i("ReplicatorChange", "ERROR:: ${error.code} - ${error.message}")
                }
                Log.i("ReplicatorChange", "INFORMATION:: Checking replication progress...")
                if (
                  replicatorChange.status.progress.completed ==
                  replicatorChange.status.progress.total ||
                  replicatorChange.status.progress.completed.toInt() == 0
                ) {
                  replicationProgress.value = "Completed"
                  Log.i("ReplicatorChange", "INFORMATION:: returned STATUS Completed...")
                } else {
                  replicationProgress.value =
                    "${replicatorChange.status.progress.total / replicatorChange.status.progress.completed}"
                }
              }
            }
          }
          Log.i("ReplicatorChange", "INFORMATION:: Replication Started method completed...")
        }

        else -> {
          Log.i("ReplicatorChange", "INFORMATION:: Stopping replication...")
          replicatorService.stopReplication()
        }
      }
    }
  }

  private val replicatorConfig = replicatorService.replicationConfig.value
  val username: String = replicatorConfig.username
  val password: String = replicatorConfig.password
  val continuousState = mutableStateOf(replicatorConfig.continuous)
  val selfSignedCertState = mutableStateOf(replicatorConfig.selfSignedCert)
  val replicatorTypes = replicatorService.replicationTypes
  val replicatorTypeSelected = mutableStateOf(replicatorConfig.replicatorType)
  val serverUrlState = mutableStateOf(replicatorConfig.endpointUrl)
  val heartBeatState = mutableLongStateOf(replicatorConfig.heartBeat)

  val onContinuousChanged: (Boolean) -> Unit = { newValue -> continuousState.value = newValue }

  val onSelfSignedCertChanged: (Boolean) -> Unit = { newValue ->
    selfSignedCertState.value = newValue
  }

  val onReplicatorTypeChanged: (String) -> Unit = { newValue ->
    replicatorTypeSelected.value = newValue
  }

  val onServerUrlChanged: (String) -> Unit = { newValue -> serverUrlState.value = newValue }

  val onHeartBeatChanged: (Long) -> Unit = { newValue -> heartBeatState.value = newValue }

  fun save() {
    //        Log.i("ReplicatorConfigViewModel.", replicatorService.toString())
    viewModelScope.launch(Dispatchers.IO) {
      val config =
        ReplicatorConfig(
          username = username,
          password = password,
          endpointUrl = serverUrlState.value,
          replicatorType = replicatorTypeSelected.value,
          heartBeat = heartBeatState.longValue,
          continuous = continuousState.value,
          selfSignedCert = selfSignedCertState.value,
        )
      try {
        Log.i("ReplicatorViewModel.replicatorService", replicatorService.toString())

        replicatorService.updateReplicationConfig(config)

        Log.i(
          "ReplicatorViewModel.replicatorService",
          "canStartReplication: ${replicatorService.canStartReplication}",
        )

        val userProfile = userProfileManager.getCurrentUser()
        userProfile.endpointUrl = config.endpointUrl
        Log.d("ReplicatorViewModel", "user profile endpointUrl: ${userProfile.endpointUrl}")
        userProfileManager.updateUserProfile(userProfile)

      } catch (e: Exception) {
        //                TODO("throw update the UI with error that states replication is running
        // can't perform this until it's stopped")
      }
    }
  }
}
