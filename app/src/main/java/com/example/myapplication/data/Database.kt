package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [ExerciseModel::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun Dao(): Dao

    class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.Dao()

                    // Delete all content here.
                    // dao.deleteAll()

                    // Add sample words.
                    var exercise = ExerciseModel(
                        name = "90 Lat Stretch",
                        exerciseCategory = "Stretch",
                        bodyPart = "Back"
                    )
                    dao.insertExercise(exercise)
                    exercise = ExerciseModel(
                        name = "Bench Press",
                        exerciseCategory = "Compound",
                        bodyPart = "Chest"
                    )
                    dao.insertExercise(exercise)
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
