package com.example.myapplication.components.importWorkoutProgram

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

// TODO create function to import workout program
// TODO create function to import program with no mesocycles
// TODO create function to import program with mesocycles
// TODO create function to import program with microcycles
// TODO create function to add to existing program

class ImportWorkoutProgram(
  //  private val manageExercisesUseCase: ManageExercisesUseCase,
  //  private val importWorkoutProgramRepository: ImportWorkoutProgramRepository,
  private val getExerciseObjectID: (exerciseName: String) -> ObjectId?
) {
  //  fun getExerciseObjectID(exerciseName: String): ObjectId {
  //    /* return manageExercisesUseCase.getExerciseByName(exerciseName)*/
  //    return ObjectId()
  //  }

  fun createExerciseSets(
    pklExercisesSets: List<AdvancedProgramTemplate.ExerciseSets>
    //    getExerciseObjectID: (exerciseName: String) -> ObjectId?,
  ): RealmList<ExerciseSets> {
    val exercisesSets = mutableListOf<ExerciseSets>()

    pklExercisesSets.forEachIndexed { index, pklExerciseSets ->
      val exerciseSets =
        ExerciseSets().apply {
          // exSetsUUID = _exSetsUUID
          exerciseOrder = index.plus(1)
          exerciseID = getExerciseObjectID(pklExerciseSets.name)
          sets =
            if (pklExerciseSets.sets > 0) {
              val setList = mutableListOf<Set>()
              for (indexSet in 0..<pklExerciseSets.sets.toInt()) {
                val set =
                  Set().apply {
                    setNumber = indexSet.plus(1)
                    targetRepetitions =
                      targetRepetitions(
                        pklExerciseSets.minRepetitions,
                        pklExerciseSets.maxRepetitions,
                      )

                    targetRepetitionsInReserve =
                      pklExerciseSets.targetRepetitionsInReserve?.toDouble()
                    targetRatePerceivedEffort =
                      pklExerciseSets.targetRatePerceivedEffort?.toDouble()
                    percentage1RM = pklExerciseSets.percentage1RM
                  }
                //                println(
                //                  "ImportWorkoutProgram.createExerciseSets: ${
                //                    Json.encodeToString(
                //                      Set.serializer(),
                //                      set
                //                    )
                //                  }"
                //                )
                setList.add(set)
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
      //      print("ImportWorkoutProgram.createWorkout: ${pklWorkout.name}")

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

      //      println("ImportWorkoutProgram.createAdvancedExerciseSets
      // ${pklAdvancedExerciseSets.name}")

      if (pklAdvancedExerciseSets.setScheme != null) {
        val setScheme = pklAdvancedExerciseSets.setScheme

        when {
          setScheme.repetitions.isNotEmpty() -> {
            if (setScheme.sets.toInt() != setScheme.repetitions.size) {
              throw Exception(
                "Repetitions List (${setScheme.repetitions.size}) is not the same size as the number of Sets (${setScheme.sets.toInt()})"
              )
              // return@forEachIndexed
            }
          }
          setScheme.sets.toInt() != setScheme.targetRepetitionsInReserve.size -> {
            throw Exception("Target RIR List is not the same size as the number of Sets")
            // return@forEachIndexed
          }
          setScheme.sets.toInt() != setScheme.targetRatePerceivedEffort.size -> {
            throw Exception("Target RPE List is not the same size as the number of Sets")
            // return@forEachIndexed
          }
          setScheme.percentage1RM.isNotEmpty() -> {
            if (setScheme.sets.toInt() != setScheme.percentage1RM.size) {
              throw Exception("Percentage1RM List is not the same size as the number of Sets")
              // return@forEachIndexed
            }
          }
          else -> {

            val exercise = getExerciseObjectID(pklAdvancedExerciseSets.name)
            val exerciseSets =
              ExerciseSets().apply {
                // exSetsUUID = _exSetsUUID
                exerciseOrder = index.plus(1)
                exerciseID = exercise
                // TODO search database for exercise & set exerciseID
                sets = createSetsFromSetScheme(setScheme, exercise)
              }

            //          println(
            //            "ImportWorkoutProgram.createAdvancedExerciseSets
            // ${Json.encodeToString(ExerciseSets.serializer(), exerciseSets)}"
            //          )

            exercisesSets.add(index, exerciseSets)
          }
        }
      }
    }

    return exercisesSets.toRealmList()
  }

  private fun createSetsFromSetScheme(
    pklSetScheme: AdvancedProgramTemplate.SetScheme,
    exercise: ObjectId?,
  ) =
    if (pklSetScheme.sets > 0) {
      val setList = mutableListOf<Set>()

      //      println("ImportWorkoutProgram.createSets:
      // ${pklAdvancedExerciseSets.setScheme.sets.toInt()}")
      for (index in 0..<pklSetScheme.sets.toInt()) {
        //        println("ImportWorkoutProgram.createSets: ${exercise}")
        //        println("ImportWorkoutProgram.createSets: ${pklAdvancedExerciseSets.setScheme}")
        //        println("ImportWorkoutProgram.createSets: $index")
        val set =
          Set().apply {
            setNumber = index.plus(1)
            repetitions =
              if (pklSetScheme.repetitions.isNotEmpty()) {
                pklSetScheme.repetitions[index].toInt()
              } else null

            targetWeight =
              if (pklSetScheme.targetWeight.isNotEmpty()) {
                pklSetScheme.targetWeight[index]
              } else null

            targetRepetitions =
              targetRepetitions(pklSetScheme.minRepetitions, pklSetScheme.maxRepetitions)
            targetRepetitionsInReserve =
              if (pklSetScheme.targetRepetitionsInReserve.isNotEmpty()) {
                pklSetScheme.targetRepetitionsInReserve[index].toDouble()
              } else null

            targetRatePerceivedEffort =
              if (pklSetScheme.targetRatePerceivedEffort.isNotEmpty()) {
                pklSetScheme.targetRatePerceivedEffort[index].toDouble()
              } else null

            percentage1RM =
              if (pklSetScheme.percentage1RM.isNotEmpty()) pklSetScheme.percentage1RM[index]
              else null

            // TODO Determine progressSchemeID
            //            progressionSchemeID =
            //              pklAdvancedExerciseSets.setScheme.progressionSchemeID?.toInt() ?: 0

            exerciseID = exercise
          }
        //        println(
        //          "ImportWorkoutProgram.createSets: set:" + Json.encodeToString(Set.serializer(),
        // set)
        //        )
        setList.add(index, set)
      }

      setList.toRealmList()
    } else {
      realmListOf<Set>()
    }

  private fun targetRepetitions(minRepetitions: Long?, maxRepetitions: Long?) =
    when {
      minRepetitions != null && maxRepetitions != null -> {
        if (minRepetitions == maxRepetitions) {
          "$maxRepetitions"
        } else if (minRepetitions < maxRepetitions) {
          "${minRepetitions}-${maxRepetitions}"
        } else {
          "${maxRepetitions}-${minRepetitions}"
        }
      }
      minRepetitions != null && maxRepetitions == null -> {
        "${minRepetitions}+"
      }
      else -> {
        null
      }
    }

  fun createAdvancedWorkout(
    pklAdvancedWorkouts: List<AdvancedProgramTemplate.AdvancedWorkout>
  ): RealmList<Workout> {
    val workouts = mutableListOf<Workout>()

    pklAdvancedWorkouts.forEachIndexed { index, pklAdvancedWorkout ->
      //      print("ImportWorkoutProgram.createAdvancedWorkout: ${pklAdvancedWorkout.name}\n")

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
      //      println("ImportWorkoutProgram.createAdvancedWorkout:
      // ${Json.encodeToString(Workout.serializer(), workout)}")
      workouts.add(index, workout)
    }
    return workouts.toRealmList()
  }

  fun createMicrocyles(
    pklMicrocycles: List<AdvancedProgramTemplate.Microcycle>
  ): RealmList<Microcycle> {
    val microcycles = mutableListOf<Microcycle>()

    pklMicrocycles.forEachIndexed { index, pklMicrocycle ->
      //      print("ImportWorkoutProgram.createMicrocyles: ${pklMicrocycle.name}\n")

      val microcycle =
        Microcycle().apply {
          name = pklMicrocycle.name
          description = pklMicrocycle.description
          order = pklMicrocycle.order.toInt()
          workouts =
            if (!pklMicrocycle.workouts.isNullOrEmpty()) {
              createWorkout(pklMicrocycle.workouts)
            } else if (!pklMicrocycle.advancedWorkouts.isNullOrEmpty()) {
              createAdvancedWorkout(pklMicrocycle.advancedWorkouts)
            } else {
              realmListOf<Workout>()
            }
        }
      //      println("ImportWorkoutProgram.createMicrocyles:
      // ${Json.encodeToString(Microcycle.serializer(), microcycle)}")
      microcycles.add(index, microcycle)
    }

    return microcycles.toRealmList()
  }

  fun createMesocycles(
    pklMesocycles: List<AdvancedProgramTemplate.Mesocycle>
  ): RealmList<Mesocycle> {
    val mesocycles = mutableListOf<Mesocycle>()
    pklMesocycles.forEachIndexed { index, pklMesocycle ->
      //      println(pklMesocycle.name)
      val mesocycle =
        Mesocycle().apply {
          name = pklMesocycle.name
          description = pklMesocycle.description
          order = pklMesocycle.order.toInt()
          microcycles = createMicrocyles(pklMesocycle.microcycles)
        }
      //      println(
      //        "ImportWorkoutProgram.createMesocycles: ${
      //          Json.encodeToString(
      //            Mesocycle.serializer(),
      //            mesocycle
      //          )
      //        }"
      //      )
      mesocycles.add(index, mesocycle)
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

  fun importAdvancedWorkoutProgram(pklProgram: AdvancedProgramTemplate.Program): Program {
    return createProgram(pklProgram)

    //    println(program)
  }

  fun importMesocycles(
    pklMesocycles: List<AdvancedProgramTemplate.Mesocycle>
  ): RealmList<Mesocycle> {
    return createMesocycles(pklMesocycles)
  }

  fun importMicrocycles(
    pklMicrocycles: List<AdvancedProgramTemplate.Microcycle>
  ): RealmList<Microcycle> {
    return createMicrocyles(pklMicrocycles)
  }

  fun importWorkouts(pklWorkouts: List<AdvancedProgramTemplate.Workout>): RealmList<Workout> {
    return createWorkout(pklWorkouts)
  }

  fun importAdvancedWorkouts(
    pklAdvancedWorkouts: List<AdvancedProgramTemplate.AdvancedWorkout>
  ): RealmList<Workout> {
    return createAdvancedWorkout(pklAdvancedWorkouts)
  }
}
