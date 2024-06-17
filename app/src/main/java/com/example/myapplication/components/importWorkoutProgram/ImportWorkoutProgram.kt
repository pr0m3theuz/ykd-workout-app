package com.example.myapplication.components.importWorkoutProgram

import AdvancedProgramTemplate
import com.example.myapplication.components.ManageWorkoutUseCase
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import org.mongodb.kbson.ObjectId
import org.pkl.config.java.ConfigEvaluator
import org.pkl.config.kotlin.forKotlin
import org.pkl.config.kotlin.to
import org.pkl.core.ModuleSource
import java.io.File

// TODO create function to import workout program
// TODO create function to import program with no mesocycles
// TODO create function to import program with mesocycles
// TODO create function to import program with microcycles

class ImportWorkoutProgramTest(private val manageWorkoutUseCase: ManageWorkoutUseCase) {
  fun getExerciseObjectID(exerciseName: String): ObjectId {
    return manageWorkoutUseCase.getExerciseByName(exerciseName)
  }

  fun createExerciseSets(
    pklExercisesSets: List<AdvancedProgramTemplate.ExerciseSets>
  ): RealmList<ExerciseSets> {
    val exercisesSets = mutableListOf<ExerciseSets>()

    pklExercisesSets.forEachIndexed { index, pklExerciseSets ->
      val exerciseSets =
        ExerciseSets().apply {
          //          exSetsUUID = _exSetsUUID
          exerciseOrder = index
          exerciseID = getExerciseObjectID(pklExerciseSets.name)
          sets =
            if (pklExerciseSets.sets > 0) {
              val setList = mutableListOf<Set>()
              for (set in 1..pklExerciseSets.sets) {
                Set().apply {
                  setNumber = set.toInt()
                  targetRepetitions =
                    "${pklExerciseSets.minRepetitions} - ${pklExerciseSets.maxRepetitions}"
                  targetRepetitionsInReserve = pklExerciseSets.targetRepetitionsInReserve.toDouble()
                  targetRatePerceivedEffort = pklExerciseSets.targetRatePerceivedEffort.toDouble()
                  percentage1RM = pklExerciseSets.percentage1RM
                }
              }

              setList.toRealmList()
            } else {
              realmListOf<Set>()
            }
        }
      exercisesSets.add(index, exerciseSets)
    }

    return exercisesSets.toRealmList()
  }

  fun createWorkout(pklWorkouts: List<AdvancedProgramTemplate.Workout>): RealmList<Workout> {
    val workouts = mutableListOf<Workout>()

    pklWorkouts.forEachIndexed { index, pklWorkout ->
      val workout =
        Workout().apply {
          name = pklWorkout.name
          notes = realmListOf()
          day = pklWorkout.day.toInt()
          exercisesSets = createExerciseSets(pklWorkout.exercisesSets)
        }
      workouts.add(index, workout)
    }

    return workouts.toRealmList()
  }

  fun createAdvancedExerciseSets(
    pklAdvancedExercisesSets: List<AdvancedProgramTemplate.AdvancedExerciseSets>
  ): RealmList<ExerciseSets> {
    val exercisesSets = mutableListOf<ExerciseSets>()

    pklAdvancedExercisesSets.forEachIndexed { index, pklAdvancedExerciseSets ->
      // exSetsUUID = _exSetsUUID
      // TODO search database for exercise & set exerciseID
      when {
        pklAdvancedExerciseSets.setScheme.sets.toInt() !=
                pklAdvancedExerciseSets.setScheme.repetitions.size -> {
          // TODO return error
          return@forEachIndexed
        }

        pklAdvancedExerciseSets.setScheme.sets.toInt() !=
                pklAdvancedExerciseSets.setScheme.targetRepetitionsInReserve.size -> {
          // TODO return error
          return@forEachIndexed
        }

        pklAdvancedExerciseSets.setScheme.sets.toInt() !=
                pklAdvancedExerciseSets.setScheme.targetRatePerceivedEffort.size -> {
          // TODO return error
          return@forEachIndexed
        }

        pklAdvancedExerciseSets.setScheme.sets.toInt() !=
                pklAdvancedExerciseSets.setScheme.percentage1RM.size -> {
          // TODO return error
          return@forEachIndexed
        }

        else -> {
          val exerciseSets =
            ExerciseSets().apply {
              // exSetsUUID = _exSetsUUID
              exerciseOrder = index
              exerciseID = getExerciseObjectID(pklAdvancedExerciseSets.name)
              // TODO search database for exercise & set exerciseID
              sets =
                if (pklAdvancedExerciseSets.setScheme.sets > 0) {
                  val setList = mutableListOf<Set>()
                  for (set in 1..pklAdvancedExerciseSets.setScheme.sets.toInt()) {
                    Set().apply {
                      setNumber = set
                      targetRepetitions =
                        "${pklAdvancedExerciseSets.setScheme.minRepetitions} - ${pklAdvancedExerciseSets.setScheme.maxRepetitions}"
                      targetRepetitionsInReserve =
                        pklAdvancedExerciseSets.setScheme.targetRepetitionsInReserve[set].toDouble()
                      targetRatePerceivedEffort =
                        pklAdvancedExerciseSets.setScheme.targetRatePerceivedEffort[set].toDouble()
                      percentage1RM = pklAdvancedExerciseSets.setScheme.percentage1RM[set]
                    }
                  }

                  setList.toRealmList()
                } else {
                  realmListOf<Set>()
                }
            }
          exercisesSets.add(index, exerciseSets)
        }
      }
    }

    return exercisesSets.toRealmList()
  }

  fun createAdvancedWorkout(
    pklAdvancedWorkouts: List<AdvancedProgramTemplate.AdvancedWorkout>
  ): RealmList<Workout> {
    val workouts = mutableListOf<Workout>()

    pklAdvancedWorkouts.forEachIndexed { index, pklAdvancedWorkout ->
      val workout =
        Workout().apply {
          name = pklAdvancedWorkout.name
          notes = realmListOf()
          day = pklAdvancedWorkout.day.toInt()
          exercisesSets =
            if (pklAdvancedWorkout.exercisesSets.isNotEmpty()) {
              createExerciseSets(pklAdvancedWorkout.exercisesSets)
            } else if (pklAdvancedWorkout.advancedExercisesSets.isNotEmpty()) {
              createAdvancedExerciseSets(pklAdvancedWorkout.advancedExercisesSets)
            } else {
              realmListOf<ExerciseSets>()
            }
        }
      workouts.add(index, workout)
    }

    return workouts.toRealmList()
  }

  fun createMicrocyles(
    pklMicrocycles: List<AdvancedProgramTemplate.Microcycle>
  ): RealmList<Microcycle> {
    val microcycles = mutableListOf<Microcycle>()

    pklMicrocycles.forEachIndexed { index, pklMicrocycle ->
      val microcycle =
        Microcycle().apply {
          name = pklMicrocycle.name
          description = pklMicrocycle.description
          order = pklMicrocycle.order.toInt()
          workouts =
            if (pklMicrocycle.workouts.isNotEmpty()) {
              createWorkout(pklMicrocycle.workouts)
            } else if (pklMicrocycle.advancedWorkouts.isNotEmpty()) {
              createAdvancedWorkout(pklMicrocycle.advancedWorkouts)
            } else {
              realmListOf<Workout>()
            }
        }

      microcycles.add(index, microcycle)
    }

    return microcycles.toRealmList()
  }

  fun createMesocycles(
    pklMesocycles: List<AdvancedProgramTemplate.Mesocycle>
  ): RealmList<Mesocycle> {
    val mesocycles = mutableListOf<Mesocycle>()
    pklMesocycles.forEachIndexed { index, pklMesocycle ->
      mesocycles.add(
        index,
        Mesocycle().apply {
          name = pklMesocycle.name
          description = pklMesocycle.description
          order = pklMesocycle.order.toInt()
          microcycles = createMicrocyles(pklMesocycle.microcycles)
        },
      )
    }
    return mesocycles.toRealmList()
  }

  fun createProgram(pklProgram: AdvancedProgramTemplate.Program): Program {
    return Program().apply {
      name = pklProgram.name
      author = pklProgram.arthur
      shortDescription = pklProgram.shortDescription
      description = pklProgram.description
      programLength = pklProgram.programLength
      uri = pklProgram.uri
      level = pklProgram.level
      programEquipment = pklProgram.programEquipment
      daysPerWeek = pklProgram.daysPerWeek
      mesocycles = createMesocycles(pklProgram.mesocycles)
    }
  }

  fun importWorkoutProgram() {
    val workoutProgram =
      File(
        "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/MAX_PLAN_AdvancedProgramTemplate.pkl"
      )
        .readText()

    val config =
      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
        evaluator.evaluate(ModuleSource.text(workoutProgram.trimIndent()))
      }

    val mesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()

//    println("\n\n${mesocycle.name}\n\n")
  }
}
