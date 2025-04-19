package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Set
import org.bson.types.ObjectId

interface CompletedRepository {
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?
}