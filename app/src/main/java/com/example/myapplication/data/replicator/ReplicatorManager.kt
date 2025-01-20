package com.example.myapplication.data.replicator

import com.couchbase.lite.Database
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorConfiguration

//handle pointers to replicator and database associated with it
class ReplicatorManager(var database: Database) {
  var replicator: Replicator? = null
  var replicatorConfiguration: ReplicatorConfiguration? = null
}