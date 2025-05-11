package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.util.LoadRepCapacity
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity
import kotlinx.coroutines.flow.Flow

interface ModernizedLoadRIRReposistory {
  fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    repsPerformed: Int
  ): ModernizedLoadRIR?

  fun getRIRRPE(
    repCapacity: RepCapacity,
    repsPerformed: Int,
    relativeLoadPercent: Double
  ): ModernizedLoadRIR?

  fun getRepsPerformed(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    relativeLoadPercent: Double
  ): ModernizedLoadRIR?

  fun getRepCapacity(
    estimated1RMPercent: Double,
  ): LoadRepCapacity?

  fun getAllModernizedLoadRIR(repCapacity: RepCapacity): Flow<List<ModernizedLoadRIR>>

  fun getModernizedLoadRIR(
    repCapacity: RepCapacity,
    targetPercentage: Double?,
    targetRIR: Double?,
    targetRPE: Double?,
    targetReps: Int?
  ): List<ModernizedLoadRIR>?
}