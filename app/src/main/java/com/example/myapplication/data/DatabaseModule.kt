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
    fun provideDao(database: AppRoomDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppRoomDatabase {
        return Room.databaseBuilder(
            app,
            AppRoomDatabase::class.java,
            "app_database.db"
        )
//            .createFromAsset("database/test.db")
            .build()
    }
}
