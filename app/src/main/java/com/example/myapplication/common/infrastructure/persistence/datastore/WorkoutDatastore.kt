package com.example.myapplication.common.infrastructure.persistence.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.myapplication.datastore.ActiveWorkout // Import the generated protobuf class
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

// Create the DataStore instance using the dataStore delegate
private val Context.activeWorkoutStore: DataStore<ActiveWorkout> by dataStore(
    fileName = "active_workout.pb",
    serializer = ActiveWorkoutSerializer
)

class WorkoutDatastore(private val context: Context) {

    val getActiveWorkout: Flow<ActiveWorkout> = context.activeWorkoutStore.data
        .catch { exception ->
            // Handle IO exceptions when reading data
            if (exception is IOException) {
                emit(ActiveWorkout.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun saveActiveWorkout(activeWorkout: ActiveWorkout) {
        context.activeWorkoutStore.updateData { savedWorkout ->
            // currentSettings is the current ActiveWorkout object stored in DataStore
            // You merge the new activeWorkout with the current one or simply replace it
            // For simplicity, this example replaces the current settings with the new one.
            activeWorkout
//            savedWorkout
        }
    }
    
    suspend fun clearActiveWorkout() {
        context.activeWorkoutStore.updateData {
            ActiveWorkout.getDefaultInstance()
        }
    }
}
