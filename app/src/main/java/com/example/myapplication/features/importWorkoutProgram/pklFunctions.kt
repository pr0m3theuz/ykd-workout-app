package com.example.myapplication.features.importWorkoutProgram

//import android.os.Build
//import androidx.annotation.RequiresApi
//import org.pkl.config.java.ConfigEvaluator
//import org.pkl.config.kotlin.forKotlin
//import org.pkl.core.ModuleSource
//import java.io.File
//
//
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//fun importWorkoutProgram() {
//  val workoutProgram =
//    File("/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/main/java/com/example/myapplication/MAX_PLAN_AdvancedProgramTemplate.pkl").readText()
//
//  val config = ConfigEvaluator.preconfigured().forKotlin().use { evaluator ->
//    evaluator.evaluate(ModuleSource.text(workoutProgram.trimIndent()))
//  }
//
//  println(config["mesocycles"])
//}
//
