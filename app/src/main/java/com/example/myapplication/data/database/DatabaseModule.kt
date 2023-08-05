package com.example.myapplication.data.database

/*@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
*/
/* @Provides
    fun provideExerciseDao(database: AppRoomDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideExerciseNotesDao(database: AppRoomDatabase): ExerciseNotesDao {
        return database.exerciseNotesDao()
    }

    @Provides
    fun provideWorkoutNotesDao(database: AppRoomDatabase): WorkoutNotesDao {
        return database.workoutNotesDao()
    }

    @Provides
    fun provideMicrocycleNotesDao(database: AppRoomDatabase): MicrocycleNotesDao {
        return database.microcycleNotesDao()
    }

    @Provides
    fun provideMesocycleNotesDao(database: AppRoomDatabase): MesocycleNotesDao {
        return database.mesocycleNotesDao()
    }

    @Provides
    fun provideProgramNotesDao(database: AppRoomDatabase): ProgramNotesDao {
        return database.programNotesDao()
    }

    @Provides
    fun provideProgramsDao(database: AppRoomDatabase): ProgramsDao {
        return database.programsDao()
    }

    @Provides
    fun provideMesocyclesDao(database: AppRoomDatabase): MesocyclesDao {
        return database.mesocyclesDao()
    }

    @Provides
    fun provideMicrocyclesDao(database: AppRoomDatabase): MicrocyclesDao {
        return database.microcyclesDao()
    }

    @Provides
    fun provideWorkoutDao(database: AppRoomDatabase): WorkoutsDao {
        return database.workoutsDao()
    }

    @Provides
    fun provideSetsDao(database: AppRoomDatabase): SetsDao {
        return database.setsDao()
    }

    @Provides
    fun provideExerciseSetsDao(database: AppRoomDatabase): ExerciseSetsDao {
        return database.exerciseSetsDao()
    }
*/
/* @Provides
 @Singleton
 fun provideDatabase(app: Application): Realm {
   val configuration =
     RealmConfiguration.Builder(
         schema =
           setOf(
             Exercise::class,
             Set::class,
             Workout::class,
             Program::class,
             Mesocycle::class,
             Microcycle::class,
             ExerciseNote::class,
             WorkoutNote::class,
             ProgramNote::class,
             MesocycleNote::class,
             MicrocycleNote::class,
             MuscleExercise::class,
             MuscleRoleFunction::class,
             Muscle::class,
             MuscleGroup::class,
             Equipment::class,
             Utility::class,
             Force::class,
             Mechanic::class,
             //            ProgressionScheme::class,
           )
       )
       .name("realm_database.db")
       .build()

     Realm.open(configuration)

 }
}
*/
