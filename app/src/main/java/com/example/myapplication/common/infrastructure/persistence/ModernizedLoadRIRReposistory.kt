package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.util.LoadRepCapacity
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity

interface ModernizedLoadRIRReposistory {
  suspend fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    repsPerformed: Int
  ): ModernizedLoadRIR?

  suspend fun getRIRRPE(
    repCapacity: RepCapacity,
    repsPerformed: Int,
    relativeLoadPercent: Double
  ): ModernizedLoadRIR?

  suspend fun getRepsPerformed(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    relativeLoadPercent: Double
  ): ModernizedLoadRIR?

  suspend fun getRepCapacity(
    estimated1RMPercent: Double,
  ): LoadRepCapacity?
}