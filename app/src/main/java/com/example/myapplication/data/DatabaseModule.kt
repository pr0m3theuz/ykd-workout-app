package com.example.myapplication.data

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
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
    fun provideWorkoutDao(database: AppRoomDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideSetsDao(database: AppRoomDatabase): SetsDao {
        return database.setsDao()
    }

    @Provides
    fun provideExerciseSetsDao(database: AppRoomDatabase): ExerciseSetsDao {
        return database.exerciseSetsDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppRoomDatabase {
        return Room.databaseBuilder(
            app,
            AppRoomDatabase::class.java,
            "app_database.db"
        )
            .createFromAsset("database/test_v3.db")
            .build()
    }
}
