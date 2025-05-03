package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.util.ModernizedLoadRIRItem
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.RepCapacityLookupData

interface ModernizedLoadRIRReposistory {
  suspend fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    repsPerformed: Int
  ): ModernizedLoadRIRItem?

  suspend fun getRIRRPE(
    repCapacity: RepCapacity,
    repsPerformed: Int,
    relativeLoadPercent: Double
  ): ModernizedLoadRIRItem?

  suspend fun getRepsPerformed(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    relativeLoadPercent: Double
  ): ModernizedLoadRIRItem?

  suspend fun getRepCapacity(
    estimated1RMPercent: Double,
  ): RepCapacityLookupData?
}