package com.example.myapplication.common.infrastructure.persistence.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.myapplication.datastore.ActiveWorkout // Import the generated protobuf class
import com.example.myapplication.datastore.ActiveWorkoutPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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


private val Context.activeWorkoutPreferencesStore: DataStore<ActiveWorkoutPreferences> by dataStore(
  fileName = "active_workouts.pb",
  serializer = ActiveWorkoutPreferencesSerializer
)

class ActiveWorkoutsDatastore(private val context: Context) {

  val getActiveWorkouts: Flow<ActiveWorkoutPreferences> = context.activeWorkoutPreferencesStore.data
    .catch { exception ->
      // Handle IO exceptions when reading data
      if (exception is IOException) {
        emit(ActiveWorkoutPreferences.getDefaultInstance())
      } else {
        throw exception
      }
    }

  fun getActiveWorkoutById(workoutId: String): Flow<ActiveWorkout?> {
    return context.activeWorkoutPreferencesStore.data.map { preferences ->
      // Looks up the specific workout by its ID in the map
      preferences.activeWorkoutsMap[workoutId]
    }
  }

  suspend fun saveActiveWorkoutById(workoutId: String, activeWorkout: ActiveWorkout) {
    context.activeWorkoutPreferencesStore.updateData { currentPreferences ->
      currentPreferences.toBuilder()
        // .putActiveWorkouts adds it if it's new, or overwrites it if the ID exists
        .putActiveWorkouts(workoutId, activeWorkout)
        .build()
    }
  }


  suspend fun clearActiveWorkout(workoutId: String) {
    context.activeWorkoutPreferencesStore.updateData { currentPreferences ->
      currentPreferences.toBuilder()
        // Removes only the specific workout from the map
        .removeActiveWorkouts(workoutId)
        .build()
    }
  }
}
