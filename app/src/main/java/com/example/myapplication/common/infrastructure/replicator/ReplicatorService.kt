package com.example.myapplication.common.infrastructure.replicator

import androidx.compose.runtime.MutableState
import com.couchbase.lite.ReplicatorChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ReplicatorService {
  val replicationConfig: MutableState<ReplicatorConfig>
  val replicationStatus: StateFlow<String>
  var isReplicationStarted: Boolean
  val replicationTypes: List<String>
  val canStartReplication: StateFlow<Boolean>

  fun updateReplicationConfig(replicationConfig: ReplicatorConfig)
  fun calculateReplicationStatus()
  fun updateReplicationStatus(status: String)

  fun startReplication()
  fun stopReplication()
  fun updateAuthentication(isReset: Boolean)
  fun getReplicatorChangeFlow(): Flow<ReplicatorChange>?
}