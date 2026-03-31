package com.example.myapplication.common.infrastructure.persistence.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.myapplication.datastore.ActiveWorkout // Import the generated protobuf class
import com.example.myapplication.datastore.ActiveWorkoutPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ActiveWorkoutSerializer : Serializer<ActiveWorkout> {
    override val defaultValue: ActiveWorkout = ActiveWorkout.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ActiveWorkout {
        try {
            return ActiveWorkout.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ActiveWorkout, output: OutputStream) {
        t.writeTo(output)
    }
}

object ActiveWorkoutPreferencesSerializer : Serializer<ActiveWorkoutPreferences> {
    override val defaultValue: ActiveWorkoutPreferences =
        ActiveWorkoutPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ActiveWorkoutPreferences {
        return try {
            ActiveWorkoutPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ActiveWorkoutPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}