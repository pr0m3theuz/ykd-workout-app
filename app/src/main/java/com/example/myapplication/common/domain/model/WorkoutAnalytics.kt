package com.example.myapplication.common.domain.model

data class WorkoutAnalytics(
  val workoutCount: Int?,
  val totalDuration: Long?,
  val totalVolumeInPounds: Double?,
  val totalVolumeInKilograms: Double?
)
