package com.example.myapplication.data.network

import com.example.myapplication.data.model.Set
import org.bson.types.ObjectId

interface CompletedRepository {
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?
}