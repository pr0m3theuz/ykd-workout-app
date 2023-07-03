package com.example.myapplication.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideDao(database: AppRoomDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            AppRoomDatabase::class.java,
            "app_database.db"
        )
//            .createFromAsset("database/app_database.db")
//            .addCallback(AppRoomDatabase.DatabaseCallback(CoroutineScope(SupervisorJob())))
            .build()
    }
}
