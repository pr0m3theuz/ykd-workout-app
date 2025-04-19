package com.example.myapplication.common.infrastructure.replicator

data class ReplicatorConfig(
  var username: String,
  var password: String,
  var endpointUrl: String,
  var replicatorType: String,
  var heartBeat: Long,
  var continuous: Boolean,
  var selfSignedCert: Boolean
)
