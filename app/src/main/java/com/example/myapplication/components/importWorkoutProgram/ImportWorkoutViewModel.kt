package com.example.myapplication.components.importWorkoutProgram

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import com.example.myapplication.data.network.ExercisesRepository

//// TODO Build Program Preview UI
//// TODO Build Import Program UI
//// TODO Implement Test/Validation for Importing Programs & Workouts

@OptIn(SavedStateHandleSaveableApi::class)
class ImportWorkoutViewModel(
  private val application: Application,
  private val exerciseRepository: ExercisesRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
//  val importWorkoutProgram = ImportWorkoutProgram(getExerciseObjectID = {
//    exerciseRepository.getExerciseByName(it)._id
//  })
//
//  fun importWorkoutProgram(content: String): Any? {
//    val program = importAdvancedWorkoutProgram(content)
//    val mesocycles = importMesocycles(content)
//    val microcycles = importMicrocycles(content)
//    val workouts = importWorkouts(content)
//    val advancedWorkouts = importAdvancedWorkouts(content)
//
//    return when {
//      program != null -> program
//      mesocycles != null -> mesocycles
//      microcycles != null -> microcycles
//      workouts != null -> workouts
//      advancedWorkouts != null -> advancedWorkouts
//      else -> null
//    }
//  }
//
// /* fun importWorkoutProgram(content: String): Any? {
//    return listOf(
//      ::importAdvancedWorkoutProgram,
//      ::importMesocycles,
//      ::importMicrocycles,
//      ::importWorkouts,
//      ::importAdvancedWorkouts
//
//    )
//      .asSequence()
//      .map { it(content) }
//      .firstOrNull { it != null }
//  }*/
//
//
//  fun importAdvancedWorkoutProgram(file: String): Program? {
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    return try {
//      val pklProgram = config["program"].to<AdvancedProgramTemplate.Program>()
//      val program: Program = importWorkoutProgram.importAdvancedWorkoutProgram(pklProgram)
//      println(Json.encodeToString(Program.serializer(), program))
//      program
//    } catch (e: Exception) {
//      null
//    }
//  }
//
//  fun importMesocycles(file: String): List<Mesocycle>? {
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    return try {
//      val pklMesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
//      val mesocycles: List<Mesocycle> = importWorkoutProgram.importMesocycles(pklMesocycles)
//      println(Json.encodeToString(Mesocycle.serializer(), mesocycles.first()))
//      mesocycles
//    } catch (e: Exception) {
//      null
//    }
//  }
//
//  fun importMicrocycles(file: String): List<Microcycle>? {
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    return try {
//      val pklMicrocycle = config["microcycles"].to<List<AdvancedProgramTemplate.Microcycle>>()
//      val microcycles: List<Microcycle> = importWorkoutProgram.importMicrocycles(pklMicrocycle)
//      println(Json.encodeToString(Microcycle.serializer(), microcycles.first()))
//      microcycles
//    } catch (e: Exception) {
//      null
//    }
//  }
//
//  fun importWorkouts(file: String): List<Workout>? {
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    return try {
//      val pklWorkouts = config["workouts"].to<List<AdvancedProgramTemplate.Workout>>()
//      val workouts: List<Workout> = importWorkoutProgram.importWorkouts(pklWorkouts)
//      println(Json.encodeToString(Workout.serializer(), workouts.first()))
//      workouts
//    } catch (e: Exception) {
//      null
//    }
//  }
//
//  fun importAdvancedWorkouts(file: String): List<Workout>? {
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    return try {
//      val pklAdvancedWorkouts =
//        config["workouts"].to<List<AdvancedProgramTemplate.AdvancedWorkout>>()
//      val advancedWorkouts: List<Workout> =
//        importWorkoutProgram.importAdvancedWorkouts(pklAdvancedWorkouts)
//      println(Json.encodeToString(Workout.serializer(), advancedWorkouts.first()))
//      advancedWorkouts
//    } catch (e: Exception) {
//      null
//    }
//  }
}
