package com.example.myapplication.common.infrastructure.database

import android.net.Uri

interface BackupManager {
  // Export current DB to a URI
  suspend fun exportDatabase(destinationUri: Uri)

  // Import DB from a URI
  suspend fun importDatabase(sourceUri: Uri)
}

class BackupManagerImpl(private val databaseManager: DatabaseManager): BackupManager {
  override suspend fun exportDatabase(destinationUri: Uri) {
    return databaseManager.exportDatabase(destinationUri)
  }

  override suspend fun importDatabase(sourceUri: Uri) {
    return databaseManager.importDatabase(sourceUri)
  }
}