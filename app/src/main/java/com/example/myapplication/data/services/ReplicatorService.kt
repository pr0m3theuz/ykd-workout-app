package com.example.myapplication.data.services

import androidx.compose.runtime.MutableState
import com.couchbase.lite.ReplicatorChange
import com.example.myapplication.data.replicator.ReplicatorConfig
import kotlinx.coroutines.flow.Flow

interface ReplicatorService {
  val replicationConfig: MutableState<ReplicatorConfig>
  val replicationStatus: MutableState<String>
  var isReplicationStarted: Boolean
  val replicationTypes: List<String>
  val canStartReplication: MutableState<Boolean>

  fun updateReplicationConfig(replicationConfig: ReplicatorConfig)
  fun calculateReplicationStatus()

  fun startReplication()
  fun stopReplication()
  fun updateAuthentication(isReset: Boolean)
  fun getReplicatorChangeFlow(): Flow<ReplicatorChange>?
}