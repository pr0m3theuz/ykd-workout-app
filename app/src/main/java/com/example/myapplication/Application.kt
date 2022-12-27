package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppRoomDatabase
import com.example.myapplication.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NameHereApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.Dao()) }
}
