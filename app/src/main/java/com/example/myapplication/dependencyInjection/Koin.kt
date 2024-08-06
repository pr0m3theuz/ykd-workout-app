package com.example.myapplication.dependencyInjection

import com.example.myapplication.components.ManageExercisesUseCase
import com.example.myapplication.components.ManageWorkoutUseCase
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.history.HistoryViewModel
import com.example.myapplication.components.importWorkoutProgram.ImportWorkoutProgram
import com.example.myapplication.components.importWorkoutProgram.ImportWorkoutViewModel
import com.example.myapplication.components.programs.ProgramsViewModel
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.components.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.components.workoutDialogs.workingSets.WorkingSetViewModel
import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.network.ExerciseNotesRepository
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.ImportWorkoutProgramRepository
import com.example.myapplication.data.network.ProgramsRepository
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutNotesRepository
import com.example.myapplication.data.network.WorkoutRepository
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// For Kotlin Mulitplatform
fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
  return startKoin {
    appDeclaration()
    modules(commonModule)
  }
}

val commonModule = module {
  /*    single<Configuration> {
      RealmConfiguration.create(
      schema =
      setOf(
          Exercise::class,
          Set::class,
          Workout::class,
          Program::class,
          Mesocycle::class,
          Microcycle::class,
          ExerciseNote::class,
          WorkoutNote::class,
          ProgramNote::class,
          MesocycleNote::class,
          MicrocycleNote::class,
          MuscleExercise::class,
          MuscleRoleFunction::class,
          Muscle::class,
          MuscleGroup::class,
          Equipment::class,
          Utility::class,
          Force::class,
          Mechanic::class,
          //            ProgressionScheme::class,
      )
  )}
  single {
      Realm.open(get<Configuration>())
  }*/

  singleOf(::RealmDatabase)
  singleOf(::WorkoutRepository)
  singleOf(::ProgramsRepository)
  singleOf(::ExercisesRepository)
  singleOf(::ExerciseNotesRepository)
  singleOf(::WorkoutNotesRepository)
  singleOf(::UserRepository)
  singleOf(::ImportWorkoutProgramRepository)
  singleOf(::ManageWorkoutUseCase)
  singleOf(::ManageExercisesUseCase)
  singleOf(::ImportWorkoutProgram)

  viewModelOf(::ProgramsViewModel)
  viewModelOf(::ExercisesViewModel)
  viewModelOf(::WorkoutViewModel)
  viewModelOf(::EquateVolumeViewModel)
  viewModelOf(::WorkingSetViewModel)
  viewModelOf(::WarmUpSetsViewModel)
  viewModelOf(::HistoryViewModel)
  viewModelOf(::ImportWorkoutViewModel)

  //  viewModel { WorkoutViewModel(get(), get(), get(), get()) }
}
