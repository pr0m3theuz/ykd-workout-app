package com.example.myapplication.common.infrastructure.database

import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfigurationFactory
import com.couchbase.lite.newConfig
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DatabaseManager(private val context: Context) {

  var database: Database? = null

  // store User Data & Active & Completed Programs & Workouts

  private val databaseName = "workouts" // "database"

  init {
    // setup couchbase lite
    CouchbaseLite.init(context)
  }

  fun initializeDatabases(
//            currentUser: User
  ) {
    Log.i("DatabaseManager", "DatabaseManager.initializeDatabases()")
    try {
      val dbConfig = DatabaseConfigurationFactory.newConfig(context.filesDir.toString())

      val bundledDatabaseName = "${databaseName}.zip"
      // create or open a database to share between team members to store
      // projects, assets, and user profiles
      // calculate database name based on current logged in users team name


      if (!Database.exists(databaseName, context.filesDir)) {
        unzip(bundledDatabaseName, File(context.filesDir.toString()))
        val dbFile = File("${context.filesDir}/${databaseName}.cblite2")
        Database.copy(dbFile, databaseName, dbConfig)
      }
      database = Database(databaseName, dbConfig)

      requireNotNull(database) { "DatabaseManager: Database was not created!" }

      val collections = listOf(
        "CompletedPrograms",
        "CompletedSets",
        "CompletedWorkouts",
        "Equipment",
        "Exercise",
        "ExerciseNote",
//        "ExerciseSets",
        "Force",
        "Mechanic",
        "Mesocycle",
        "MesocycleNote",
        "Microcycle",
        "MicrocycleNote",
        "ModernizedLoadRIR",
        "Muscle",
        "MuscleExercise",
        "MuscleGroup",
        "MuscleRoleFunction",
        "Program",
        "ProgramNote",
        "LoadRepCapacity",
//        "Set",
        "Users",
        "Utility",
        "Workout",
        "WorkoutNote"
      )

      collections.forEach { collection ->
        database!!.createCollection(collection, "_default")
      }

//      database!!.scopes.forEach { scope ->
////        Log.i("Scope", "${scope.name}")
//        scope.collections.forEach {
//          Log.i("Scope.Collection", "${scope.name}: ${it.name}")
//        }
//      }

//    create user scope
//      userDatabase = Database(userWorkoutsDatabaseName, dbConfig)
//      currentActiveWorkoutDatabase = Database(currentActiveWorkoutDatabaseName, dbConfig)
//
//      requireNotNull(userDatabase) { "DatabaseManager: User Database was not created!" }
//      requireNotNull(currentActiveWorkoutDatabase) { "DatabaseManager: Current Active Database was not created!" }
//
//      currentActiveWorkoutDatabase!!.createCollection("ActiveWorkout", "_default")
//      currentActiveWorkoutDatabase!!.createCollection("CurrentProgram", "_default")

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

  private fun unzip(
    file: String,
    destination: File
  ) {
    context.assets.open(file).use { stream ->
      val buffer = ByteArray(1024)
      val zis = ZipInputStream(stream)
      var ze: ZipEntry? = zis.nextEntry
      while (ze != null) {
        val fileName: String = ze.name
        val newFile = File(destination, fileName)
        if (ze.isDirectory) {
          newFile.mkdirs()
        } else {
          File(newFile.parent!!).mkdirs()
          val fos = FileOutputStream(newFile)
          var len: Int
          while (zis.read(buffer).also { len = it } > 0) {
            fos.write(buffer, 0, len)
          }
          fos.close()
        }
        ze = zis.nextEntry
      }
      zis.closeEntry()
      zis.close()
      stream.close()
    }
  }
}
