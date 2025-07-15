@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.common.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class WorkoutAnalytics(
  val workoutCount: Int?,
  val totalDuration: Long?,
  val totalVolumeInPounds: Double?,
  val totalVolumeInKilograms: Double?
)

data class TotalWorkoutVolume(
  val workoutEnded: Instant,
  val totalVolumeInPounds: Double,
  val totalVolumeInKilograms: Double
)