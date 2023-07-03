package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ExerciseModel::class, SetModel::class, Workout::class, Program::class,
        Mesocycle::class, Microcycle::class, ExerciseNote::class, WorkoutNote::class,
        ProgramNote::class, MesocycleNote::class, MicrocycleNote::class,
    ],
    exportSchema = false,
    version = 1,
    /*autoMigrations = [AutoMigration(from = 1, to = 2, spec = AppRoomDatabase.Migration1to2::class)]*/
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    /*    @RenameTable(fromTableName = "exercises_table", toTableName = "exercises")
        @RenameColumn(tableName = "exercises", fromColumnName = "primaryKey", toColumnName = "exerciseID")
        class Migration1to2 : AutoMigrationSpec*/

    /*class DatabaseCallback(
        private val scope: CoroutineScope,
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
//                    val exerciseDao = database.exerciseDao()
//                    exerciseDao.getAllExercises()
//                    Log.i("database onCreate(): ", "database created!")
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
    }*/

    /*companion object {
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
                "app_database_v3.db"
            )
//                .fallbackToDestructiveMigration()
//                .createFromAsset("database/test.db")
//                .addCallback(DatabaseCallback(scope))
                .build()
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }*/
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
