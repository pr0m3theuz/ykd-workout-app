package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ExerciseModel::class, SetModel::class, Workout::class, Program::class,
        Mesocycle::class, Microcycle::class, ExerciseNote::class, WorkoutNote::class,
        ProgramNote::class, MesocycleNote::class, MicrocycleNote::class,
        MuscleExercise::class, MuscleRoleFunction::class, Muscle::class,
        MuscleGroup::class, Equipment::class, Utility::class, Force::class, Mechanic::class,
        ProgressionScheme::class
    ],
    exportSchema = true,
    version = 1,
    /*autoMigrations = [AutoMigration(from = 1, to = 2, spec = AppRoomDatabase.Migration1to2::class)]*/
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
}
