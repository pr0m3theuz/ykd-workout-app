package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppRoomDatabase
import com.example.myapplication.data.Repository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class NameHereApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var database: AppRoomDatabase

    //    val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.Dao()) }
}
