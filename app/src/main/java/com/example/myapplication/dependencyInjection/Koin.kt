package com.example.myapplication.dependencyInjection

//import com.example.myapplication.components.importWorkoutProgram.ImportWorkoutProgram
//import com.example.myapplication.data.database.RealmDatabase
//import com.example.myapplication.data.network.ImportWorkoutProgramRepository
import com.example.myapplication.MainViewModel
import com.example.myapplication.components.ManageExercisesUseCase
import com.example.myapplication.components.ManageWorkoutUseCase
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.history.HistoryViewModel
import com.example.myapplication.components.profileScreen.ProfileViewModel
import com.example.myapplication.components.profileScreen.ReplicatorConfigViewModel
import com.example.myapplication.components.profileScreen.ReplicatorViewModel
import com.example.myapplication.components.programs.ProgramsViewModel
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.components.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.components.workoutDialogs.workingSets.WorkingSetViewModel
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.network.ExerciseNotesRepository
import com.example.myapplication.data.network.ExerciseNotesRepositoryImpl
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.ExercisesRepositoryImpl
import com.example.myapplication.data.network.ProgramsRepository
import com.example.myapplication.data.network.ProgramsRepositoryImpl
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.UserRepositoryImpl
import com.example.myapplication.data.network.WorkoutNotesRepository
import com.example.myapplication.data.network.WorkoutNotesRepositoryImpl
import com.example.myapplication.data.network.WorkoutRepository
import com.example.myapplication.data.network.WorkoutRepositoryImpl
import com.example.myapplication.data.services.AuthenticationService
import com.example.myapplication.data.services.AuthenticationServiceImpl
import com.example.myapplication.data.services.ReplicatorService
import com.example.myapplication.data.services.ReplicatorServiceDb
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// For Kotlin Mulitplatform
fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
  return startKoin {
    appDeclaration()
    modules(commonModule)
  }
}

@OptIn(InternalCoroutinesApi::class)
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
//  singleOf(::RealmDatabase)

  // Couchbase
  singleOf(::DatabaseManager)
  singleOf(::ReplicatorServiceDb) bind ReplicatorService::class
  singleOf(::AuthenticationServiceImpl) bind AuthenticationService::class
  singleOf(::WorkoutRepositoryImpl) bind WorkoutRepository::class
  singleOf(::ProgramsRepositoryImpl) bind ProgramsRepository::class
  singleOf(::ExercisesRepositoryImpl) bind ExercisesRepository::class
  singleOf(::ExerciseNotesRepositoryImpl) bind ExerciseNotesRepository::class
  singleOf(::WorkoutNotesRepositoryImpl) bind WorkoutNotesRepository::class
  singleOf(::UserRepositoryImpl) bind UserRepository::class
  singleOf(::ReplicatorServiceDb) bind ReplicatorService::class
//  singleOf(::ImportWorkoutProgramRepository)

  singleOf(::ManageWorkoutUseCase)
  singleOf(::ManageExercisesUseCase)
//  singleOf(::ImportWorkoutProgram)

  viewModel { MainViewModel(get(), get(), get()) }
  viewModel { ProgramsViewModel(get(), get(), get()) }
  viewModel { ExercisesViewModel(get()) }
  viewModel { WorkoutViewModel(get(), get(), get(), get()) }
  viewModel { EquateVolumeViewModel(get()) }
  viewModel { WorkingSetViewModel(get()) }
  viewModel { WarmUpSetsViewModel(get()) }
  viewModel { HistoryViewModel(get(), get(), get(), get(), get()) }
  viewModel { ProfileViewModel(get()) }
  viewModel { ReplicatorViewModel(get()) }
  viewModel { ReplicatorConfigViewModel(get()) }


//  viewModelOf(::ProgramsViewModel)
//  viewModelOf(::ExercisesViewModel)
//  viewModelOf(::WorkoutViewModel)
//  viewModelOf(::EquateVolumeViewModel)
//  viewModelOf(::WorkingSetViewModel)
//  viewModelOf(::WarmUpSetsViewModel)
//  viewModelOf(::HistoryViewModel)
//  viewModelOf(::ImportWorkoutViewModel)
//  viewModelOf(::ProfileViewModel)

  //  viewModel { WorkoutViewModel(get(), get(), get(), get()) }
}
