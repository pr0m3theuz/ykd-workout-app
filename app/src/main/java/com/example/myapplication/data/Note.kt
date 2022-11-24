package com.example.myapplication.data

import com.example.myapplication.newID

data class Note(
    val id: Long = newID(),
    var notes: String? = null,
    var pinned: Boolean = false

)
