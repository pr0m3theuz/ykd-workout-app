package com.example.myapplication.data.database

import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfigurationFactory
import com.couchbase.lite.newConfig

class DatabaseManager(private val context: Context) {

  var database: Database? = null

  // store User Data & Active & Completed Programs & Workouts
  var userDatabase: Database? = null

  // persist currently active workout
  var currentActiveWorkoutDatabase: Database? = null

  private val databaseName = "sample_mflix"// "database"
  private val userWorkoutsDatabaseName = "userDatabase"
  private val currentActiveWorkoutDatabaseName = "currentActiveWorkoutsDatabase"

  init {
    // setup couchbase lite
    CouchbaseLite.init(context)
  }

  fun initializeDatabases(
//            currentUser: User
  ) {
    try {
      val dbConfig = DatabaseConfigurationFactory.newConfig(context.filesDir.toString())

      // create or open a database to share between team members to store
      // projects, assets, and user profiles
      // calculate database name based on current logged in users team name

      database = Database(databaseName, dbConfig)

//    create user scope
      userDatabase = Database(userWorkoutsDatabaseName, dbConfig)
      currentActiveWorkoutDatabase = Database(currentActiveWorkoutDatabaseName, dbConfig)

      // setup the Database
      //            setupUserDatabase(dbConfig)

      // create indexes for database queries

    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  fun closeDatabases() {
    try {
      database?.close()
      userDatabase?.close()
      currentActiveWorkoutDatabase?.close()
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  /*  //    private fun setupUserDatabase(dbConfig: DatabaseConfiguration) {
    //        // create the warehouse database if it doesn't already exist
    //        if (!Database.exists(warehouseDatabaseName, context.filesDir)) {
    //            unzip(startingWarehouseFileName, File(context.filesDir.toString()))
    //
    //            // copy the warehouse database to the project database
    //            // never open the database directly as this will cause issues
    //            // with sync
    //            val warehouseDbFile =
    //                File(
    //                    String.format(
    //                        "%s/%s",
    //                        context.filesDir,
    //                        ("${startingWarehouseDatabaseName}.cblite2")
    //                    )
    //                )
    //            Database.copy(warehouseDbFile, warehouseDatabaseName, dbConfig)
    //        }
    //        warehouseDatabase = Database(warehouseDatabaseName, dbConfig)
    //    }
    //
    //    private fun setupCurrentActiveWorkoutDatabase(dbConfig: DatabaseConfiguration) {
    //        // create the warehouse database if it doesn't already exist
    //        if (!Database.exists(currentActiveWorkoutDatabaseName, context.filesDir)) {
    //            unzip(startingWarehouseFileName, File(context.filesDir.toString()))
    //
    //            // copy the warehouse database to the project database
    //            // never open the database directly as this will cause issues
    //            // with sync
    //            val warehouseDbFile =
    //                File(
    //                    String.format(
    //                        "%s/%s",
    //                        context.filesDir,
    //                        ("${startingWarehouseDatabaseName}.cblite2")
    //                    )
    //                )
    //            Database.copy(warehouseDbFile, currentActiveWorkoutDatabaseName, dbConfig)
    //        }
    //        currentActiveWorkoutDatabase = Database(currentActiveWorkoutDatabaseName, dbConfig)
    //    }*/

}
