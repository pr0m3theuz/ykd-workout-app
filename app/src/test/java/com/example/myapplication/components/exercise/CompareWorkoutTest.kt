package com.example.myapplication.components.exercise

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.Workout
import net.jqwik.api.Example
import org.mongodb.kbson.ObjectId

class CompareWorkoutTest {
    val realmDatabase = RealmDatabase()

    @Example
    fun `compare two workouts are the same`() {


        val workout1: Workout? = realmDatabase.getWorkoutByID(ObjectId("655a3a58d814275f8d792957"))
        val workout2: Workout? = realmDatabase.getWorkoutByID(ObjectId("655a3faa92714aa568f42a2d"))

        println(workout1 == workout2)

    }
}