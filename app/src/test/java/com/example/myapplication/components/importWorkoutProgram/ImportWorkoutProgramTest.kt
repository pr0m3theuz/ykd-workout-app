package com.example.myapplication.components.importWorkoutProgram
//
//import com.example.myapplication.data.model.ExerciseSets
//import com.example.myapplication.data.model.Mesocycle
//import com.example.myapplication.data.model.Microcycle
//import com.example.myapplication.data.model.Program
//import com.example.myapplication.data.model.Set
//import com.example.myapplication.data.model.Workout
//import io.realm.kotlin.ext.realmListOf
//import io.realm.kotlin.ext.toRealmList
//import io.realm.kotlin.types.RealmList
//import kotlinx.serialization.json.Json
//import net.jqwik.api.Example
//import org.mongodb.kbson.ObjectId
//import org.pkl.config.java.ConfigEvaluator
//import org.pkl.config.kotlin.forKotlin
//import org.pkl.config.kotlin.to
//import org.pkl.core.ModuleSource
//import java.io.File
//
//// @Config(sdk = [30])
//// @RunWith(AndroidJUnit4::class)
//class ImportWorkoutProgramTest() {
//
//  //      importWorkoutProgramRepository = ImportWorkoutProgramRepository()
//
//  @Example
//  fun `Import Workout Program`() {
//    val workoutProgram =
//      File(
//        "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/MAX_PLAN_AdvancedProgramTemplate.pkl"
//      )
//        .readText()
//
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(workoutProgram.trimIndent()))
//      }
//
//    val mesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
//
//    fun createExerciseSets(
//      pklExercisesSets: List<AdvancedProgramTemplate.ExerciseSets>
//    ): RealmList<ExerciseSets> {
//      val exercisesSets = mutableListOf<ExerciseSets>()
//
//      pklExercisesSets.forEachIndexed { index, pklExerciseSets ->
//        //        val _exSetsUUID: RealmUUID = RealmUUID.random()
//        val exerciseSets =
//          ExerciseSets().apply {
//            //          exSetsUUID = _exSetsUUID
//            exerciseOrder = index
//            // exerciseID = TODO search database for exercise & set exerciseID
//            sets =
//              if (pklExerciseSets.sets > 0) {
//                val setList = mutableListOf<Set>()
//                for (set in 1..pklExerciseSets.sets) {
//                  Set().apply {
//                    setNumber = set.toInt()
//                    targetRepetitions =
//                      "${pklExerciseSets.minRepetitions} - ${pklExerciseSets.maxRepetitions}"
//                    targetRepetitionsInReserve =
//                      pklExerciseSets.targetRepetitionsInReserve?.toDouble()
//                    targetRatePerceivedEffort =
//                      pklExerciseSets.targetRatePerceivedEffort?.toDouble()
//                    percentage1RM = pklExerciseSets.percentage1RM
//                  }
//                }
//
//                setList.toRealmList()
//              } else {
//                realmListOf<Set>()
//              }
//          }
//        exercisesSets.add(index, exerciseSets)
//      }
//
//      return exercisesSets.toRealmList()
//    }
//
//    fun createWorkout(pklWorkouts: List<AdvancedProgramTemplate.Workout>): RealmList<Workout> {
//      val workouts = mutableListOf<Workout>()
//
//      pklWorkouts.forEachIndexed { index, pklWorkout ->
//        val workout =
//          Workout().apply {
//            name = pklWorkout.name
//            notes = realmListOf()
//            day = pklWorkout.day.toInt()
//            exercisesSets = createExerciseSets(pklWorkout.exercisesSets)
//          }
//        workouts.add(index, workout)
//      }
//
//      return workouts.toRealmList()
//    }
//
//    fun getExerciseObjectID(exerciseName: String): ObjectId {
//      return ObjectId()
//    }
//
////    fun createAdvancedExerciseSets(
////      pklAdvancedExercisesSets: List<AdvancedProgramTemplate.AdvancedExerciseSets>
////    ): RealmList<ExerciseSets> {
////      val exercisesSets = mutableListOf<ExerciseSets>()
////
////      pklAdvancedExercisesSets.forEachIndexed { index, pklAdvancedExerciseSets ->
////        // val _exSetsUUID: RealmUUID = RealmUUID.random()
////
////        if (
////          pklAdvancedExerciseSets.setScheme?.sets.toInt() !=
////          pklAdvancedExerciseSets.setScheme?.repetitions.size
////        ) {
////          // TODO return error
////          return@forEachIndexed
////        } else if (
////          pklAdvancedExerciseSets.setScheme?.sets.toInt() !=
////          pklAdvancedExerciseSets.setScheme?.targetRepetitionsInReserve.size
////        ) {
////          // TODO return error
////          return@forEachIndexed
////        } else if (
////          pklAdvancedExerciseSets.setScheme?.sets.toInt() !=
////          pklAdvancedExerciseSets.setScheme?.targetRatePerceivedEffort.size
////        ) {
////          // TODO return error
////          return@forEachIndexed
////        } else if (
////          pklAdvancedExerciseSets.setScheme?.sets.toInt() !=
////          pklAdvancedExerciseSets.setScheme?.percentage1RM.size
////        ) {
////          // TODO return error
////          return@forEachIndexed
////        } else if (
////          pklAdvancedExerciseSets.setScheme?.sets.toInt() !=
////          pklAdvancedExerciseSets.setScheme?.percentage1RM.size
////        ) {
////          // TODO return error
////          return@forEachIndexed
////        }
////
////        val exerciseSets =
////          ExerciseSets().apply {
////            // exSetsUUID = _exSetsUUID
////            exerciseOrder = index
////            exerciseID = getExerciseObjectID(pklAdvancedExerciseSets.name)
////            // TODO search database for exercise & set exerciseID
////            sets =
////              if (pklAdvancedExerciseSets.setScheme?.sets > 0) {
////                val setList = mutableListOf<Set>()
////                for (set in 1..pklAdvancedExerciseSets.setScheme?.sets.toInt()) {
////                  Set().apply {
////                    setNumber = set
////                    targetRepetitions =
////                      "${pklAdvancedExerciseSets.setScheme?.minRepetitions} - ${pklAdvancedExerciseSets.setScheme?.maxRepetitions}"
////                    targetRepetitionsInReserve =
////                      pklAdvancedExerciseSets.setScheme?.targetRepetitionsInReserve[set].toDouble()
////                    targetRatePerceivedEffort =
////                      pklAdvancedExerciseSets.setScheme?.targetRatePerceivedEffort[set].toDouble()
////                    percentage1RM = pklAdvancedExerciseSets.setScheme?.percentage1RM[set]
////                  }
////                }
////
////                setList.toRealmList()
////              } else {
////                realmListOf<Set>()
////              }
////          }
////        exercisesSets.add(index, exerciseSets)
////      }
////
////      return exercisesSets.toRealmList()
////    }
//
//    fun createAdvancedWorkout(
//      pklAdvancedWorkouts: List<AdvancedProgramTemplate.AdvancedWorkout>
//    ): RealmList<Workout> {
//      val workouts = mutableListOf<Workout>()
//
//      pklAdvancedWorkouts.forEachIndexed { index, pklAdvancedWorkout ->
//        val workout =
//          Workout().apply {
//            name = pklAdvancedWorkout.name
//            notes = realmListOf()
//            day = pklAdvancedWorkout.day.toInt()
//            exercisesSets =
//              if (pklAdvancedWorkout.exercisesSets.isNotEmpty()) {
//                createExerciseSets(pklAdvancedWorkout.exercisesSets)
//              } else if (pklAdvancedWorkout.advancedExercisesSets.isNotEmpty()) {
//                realmListOf<ExerciseSets>()
////                createAdvancedExerciseSets(pklAdvancedWorkout.advancedExercisesSets)
//              } else {
//                realmListOf<ExerciseSets>()
//              }
//          }
//        workouts.add(index, workout)
//      }
//
//      return workouts.toRealmList()
//    }
//
//    fun createMicrocyles(
//      pklMicrocycles: List<AdvancedProgramTemplate.Microcycle>
//    ): RealmList<Microcycle> {
//      val microcycles = mutableListOf<Microcycle>()
//
//      pklMicrocycles.forEachIndexed { index, pklMicrocycle ->
//        val microcycle =
//          Microcycle().apply {
//            name = pklMicrocycle.name
//            description = pklMicrocycle.description
//            order = pklMicrocycle.order.toInt()
//            workouts =
//              if (!pklMicrocycle.workouts.isNullOrEmpty()) {
//                createWorkout(pklMicrocycle.workouts!!)
//              } else if (!pklMicrocycle.advancedWorkouts.isNullOrEmpty()) {
//                createAdvancedWorkout(pklMicrocycle.advancedWorkouts!!)
//              } else {
//                realmListOf<Workout>()
//              }
//          }
//
//        microcycles.add(index, microcycle)
//      }
//
//      return microcycles.toRealmList()
//    }
//
//    //    println(mesocycles.toString())
//    mesocycles.forEachIndexed { index, mesocycle ->
//      println("\n\n${mesocycle.name}\n\n")
//      Mesocycle().apply {
//        name = mesocycle.name
//        description = mesocycle.description
//        order = mesocycle.order.toInt()
//        microcycles = createMicrocyles(mesocycle.microcycles)
//      }
//    }
//  }
//
//  @Example
//  fun `Import Workout Mesocycles`() {
//    val importWorkoutProgram = ImportWorkoutProgram(getExerciseObjectID = { _ -> ObjectId() })
//
//    val file =
//      File(
//        "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/MAX_PLAN_AdvancedProgramTemplate.pkl"
//      )
//        .readText()
//
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    //    val mesocycles =
//    try {
//      val pklProgram = config["program"].to<AdvancedProgramTemplate.Program>()
////      println(program.toString())
//      val program = importWorkoutProgram.importAdvancedWorkoutProgram(pklProgram)
//      println(Json.encodeToString(Program.serializer(), program))
//    } catch (e: Exception) {
//      println(e.message)
//      println()
//    }
//
//    try {
//      val pklMesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
////      println(pklMesocycles.toString())
//      val mesocycles = importWorkoutProgram.importMesocycles(pklMesocycles)
//      mesocycles.forEach { mesocycle ->
//        println(
//          Json.encodeToString(
//            Mesocycle.serializer(),
//            mesocycle
//          )
//        )
//      }
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val microcycles = config["microcycles"].to<List<AdvancedProgramTemplate.Microcycle>>()
////      println(microcycles.toString())
//      importWorkoutProgram.importMicrocycles(microcycles)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val workouts = config["workouts"].to<List<AdvancedProgramTemplate.Workout>>()
////      println(workouts.toString())
//      importWorkoutProgram.importWorkouts(workouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val advancedWorkouts = config["workouts"].to<List<AdvancedProgramTemplate.AdvancedWorkout>>()
////      println(advancedWorkouts.toString())
//      importWorkoutProgram.importAdvancedWorkouts(advancedWorkouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//  }
//
//  @Example
//  fun `Import KONG Program`() {
//    val importWorkoutProgram = ImportWorkoutProgram(getExerciseObjectID = { _ -> ObjectId() })
//
//    val file =
//      File(
//        "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/KONG_AdvancedProgramTemplate.pkl"
//      )
//        .readText()
//
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    try {
//      val pklProgram = config["program"].to<AdvancedProgramTemplate.Program>()
////      println(program.toString())
//      val program = importWorkoutProgram.importAdvancedWorkoutProgram(pklProgram)
//      println(Json.encodeToString(Program.serializer(), program))
//    } catch (e: Exception) {
//      println(e.message)
//      println()
//    }
//
//    try {
//      val mesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
////      println(mesocycles.toString())
//      importWorkoutProgram.importMesocycles(mesocycles)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val microcycles = config["microcycles"].to<List<AdvancedProgramTemplate.Microcycle>>()
////      println(microcycles.toString())
//      importWorkoutProgram.importMicrocycles(microcycles)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val workouts = config["workouts"].to<List<AdvancedProgramTemplate.Workout>>()
////      println(workouts.toString())
//      importWorkoutProgram.importWorkouts(workouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val advancedWorkouts = config["workouts"].to<List<AdvancedProgramTemplate.AdvancedWorkout>>()
////      println(advancedWorkouts.toString())
//      importWorkoutProgram.importAdvancedWorkouts(advancedWorkouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//  }
//
//  @Example
//  fun `Import Raider Program`() {
//    val importWorkoutProgram = ImportWorkoutProgram(getExerciseObjectID = { _ -> ObjectId() })
//
//    val file =
//      File(
//        "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/Raider_ProgramTemplate.pkl"
//      )
//        .readText()
//
//    val config =
//      ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//        evaluator.evaluate(ModuleSource.text(file.trimIndent()))
//      }
//
//    try {
//      val pklProgram = config["program"].to<AdvancedProgramTemplate.Program>()
////      println(program.toString())
//      val program = importWorkoutProgram.importAdvancedWorkoutProgram(pklProgram)
//      println(Json.encodeToString(Program.serializer(), program))
//    } catch (e: Exception) {
//      println(e.message)
//      println()
//    }
//
//    try {
//      val mesocycles = config["mesocycles"].to<List<AdvancedProgramTemplate.Mesocycle>>()
////      println(mesocycles.toString())
//      importWorkoutProgram.importMesocycles(mesocycles)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val microcycles = config["microcycles"].to<List<AdvancedProgramTemplate.Microcycle>>()
//      println(microcycles.toString())
//      importWorkoutProgram.importMicrocycles(microcycles)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val workouts = config["workouts"].to<List<AdvancedProgramTemplate.Workout>>()
//      println(workouts.toString())
//      importWorkoutProgram.importWorkouts(workouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//
//    try {
//      val advancedWorkouts = config["advancedWorkouts"].to<List<AdvancedProgramTemplate.AdvancedWorkout>>()
//      println(advancedWorkouts.toString())
//      importWorkoutProgram.importAdvancedWorkouts(advancedWorkouts)
//    } catch (e: Exception) {
//      println(e.message)
//    }
//  }
//}
