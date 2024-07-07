package com.example.myapplication.components.importWorkoutProgram

import AdvancedProgramTemplate
import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.ExercisesRepository
import io.realm.kotlin.types.RealmList
import org.pkl.config.java.ConfigEvaluator
import org.pkl.config.kotlin.forKotlin
import org.pkl.config.kotlin.to
import org.pkl.core.ModuleSource

// TODO Build Program Preview UI
// TODO Build Import Program UI
// TODO Implement Test/Validation for Importing Programs & Workouts

@OptIn(SavedStateHandleSaveableApi::class)
class ImportWorkoutViewModel(
  private val application: Application,
  private val exerciseRepository: ExercisesRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  val importWorkoutProgram = ImportWorkoutProgram(getExerciseObjectID = {
    exerciseRepository.getExerciseByName(it)._id
  })

  fun importWorkoutProgram(content: String): Any? {
    return listOf(
      ::importAdvancedWorkoutProgram,
      ::importMesocycles,
      ::importMicrocycles,
      ::importWorkouts,
      ::importAdvancedWorkouts
    )
      .asSequence()
      .map { it(content) }
      .firstOrNull { it != null }
  }


  fun importAdvancedWorkoutProgram(file: String): Program? {
    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
      }

    return try {
      val pklProgram = config["program"].to<AdvancedProgramTemplate.Program>()
      val program: Program = importWorkoutProgram.importAdvancedWorkoutProgram(pklProgram)
      program
    } catch (e: Exception) {
      null
    }
  }

  fun importMesocycles(file: String): RealmList<Mesocycle>? {
    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
      }

    return try {
      val pklMesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
      val mesocycles: RealmList<Mesocycle> = importWorkoutProgram.importMesocycles(pklMesocycles)
      mesocycles
    } catch (e: Exception) {
      null
    }
  }

  fun importMicrocycles(file: String): RealmList<Microcycle>? {
    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
      }

    return try {
      val pklMicrocycle = config["microcycles"].to<List<AdvancedProgramTemplate.Microcycle>>()
      val microcycles: RealmList<Microcycle> = importWorkoutProgram.importMicrocycles(pklMicrocycle)
      microcycles
    } catch (e: Exception) {
      null
    }
  }

  fun importWorkouts(file: String): RealmList<Workout>? {
    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
      }

    return try {
      val pklWorkouts = config["workouts"].to<List<AdvancedProgramTemplate.Workout>>()
      val workouts: RealmList<Workout> = importWorkoutProgram.importWorkouts(pklWorkouts)
      workouts
    } catch (e: Exception) {
      null
    }
  }

  fun importAdvancedWorkouts(file: String): RealmList<Workout>? {
    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
      }

    return try {
      val pklAdvancedWorkouts =
        config["workouts"].to<List<AdvancedProgramTemplate.AdvancedWorkout>>()
      val advancedWorkouts: RealmList<Workout> =
        importWorkoutProgram.importAdvancedWorkouts(pklAdvancedWorkouts)
      advancedWorkouts
    } catch (e: Exception) {
      null
    }
  }
}
