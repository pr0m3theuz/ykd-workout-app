package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.GymCatUser

class UserRepository constructor(private val database: RealmDatabase) {
    fun getUser(): GymCatUser? {
        return database.getUser()
    }
}