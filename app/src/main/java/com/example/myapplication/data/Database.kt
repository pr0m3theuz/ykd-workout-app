package com.example.myapplication.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [ExerciseModel::class],
    version = 1/*, autoMigrations = [AutoMigration(from = 1, to = 2)]*/
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun Dao(): Dao

    class DatabaseCallback(
        private val scope: CoroutineScope,
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.Dao()
                    dao.getAllExercises()
                    Log.i("database onCreate(): ", "database created!")
//                     Delete all content here.
//                     dao.deleteAll()
//
//                     Add sample words.
//                    var exercise = ExerciseModel(
//                        name = "90 Lat Stretch",
//                        exerciseCategory = "Stretch",
//                        muscleGroup = "Back"
//                    )
//                    dao.insertExercise(exercise)
//                    exercise = ExerciseModel(
//                        name = "Bench Press",
//                        exerciseCategory = "Compound",
//                        muscleGroup = "Chest"
//                    )
//                    dao.insertExercise(exercise)
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
            @ApplicationContext context: Context,
            scope: CoroutineScope,
        ): AppRoomDatabase {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppRoomDatabase::class.java,
                "app_database"
            )
//                .fallbackToDestructiveMigration()
                .createFromAsset("database/test.db")
                .addCallback(DatabaseCallback(scope))
                .build()
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

/*
@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppRoomDatabase::class.java,
            "app_database"
        )
            .createFromAsset("database/app_database")
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideDao(database: AppRoomDatabase) = database.Dao()

//    @Provides
//    fun provideEntity() = ExerciseModel()
}
*/
