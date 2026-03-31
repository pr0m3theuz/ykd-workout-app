package com.example.myapplication.common.dependencyInjection

//import com.example.myapplication.components.importWorkoutProgram.ImportWorkoutProgram
//import com.example.myapplication.data.database.RealmDatabase
//import com.example.myapplication.data.network.ImportWorkoutProgramRepository
//import org.koin.android.ext.koin.androidContext
//import com.example.myapplication.common.infrastructure.persistence.InProgressWorkoutRepositoryImpl
import com.example.myapplication.MainViewModel
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.database.BackupManager
import com.example.myapplication.common.infrastructure.database.BackupManagerImpl
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.ExerciseNotesRepository
import com.example.myapplication.common.infrastructure.persistence.ExerciseNotesRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRRepository
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.ProgramsRepository
import com.example.myapplication.common.infrastructure.persistence.ProgramsRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.WorkoutNotesRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutNotesRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepositoryImpl
import com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutDatastore
import com.example.myapplication.common.infrastructure.replicator.ReplicatorService
import com.example.myapplication.common.infrastructure.replicator.ReplicatorServiceImpl
import com.example.myapplication.common.infrastructure.services.AuthenticationService
import com.example.myapplication.common.infrastructure.services.AuthenticationServiceImpl
import com.example.myapplication.features.exercises.ExercisesViewModel
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.program.ProgramsViewModel
import com.example.myapplication.features.syncData.ReplicatorConfigViewModel
import com.example.myapplication.features.syncData.ReplicatorViewModel
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import com.example.myapplication.features.workout.ManageExercisesUseCase
import com.example.myapplication.features.workout.ManagePreviewWorkoutUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.workingSets.WorkingSetViewModel
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
  singleOf(::UserProfileManager) //
//  single { UserProfileManager(androidContext(), get()) }

  singleOf(::UserRepositoryImpl) bind UserRepository::class
  singleOf(::ReplicatorServiceImpl) bind ReplicatorService::class
  singleOf(::AuthenticationServiceImpl) bind AuthenticationService::class
  singleOf(::WorkoutRepositoryImpl) bind WorkoutRepository::class
  singleOf(::ProgramsRepositoryImpl) bind ProgramsRepository::class
  singleOf(::ExercisesRepositoryImpl) bind ExercisesRepository::class
  singleOf(::ExerciseNotesRepositoryImpl) bind ExerciseNotesRepository::class
  singleOf(::WorkoutNotesRepositoryImpl) bind WorkoutNotesRepository::class
  singleOf(::ReplicatorServiceImpl) bind ReplicatorService::class
//  singleOf(::InProgressWorkoutRepositoryImpl) bind InProgressWorkoutRepository::class
  singleOf(::CompletedWorkoutRepositoryImpl) bind CompletedWorkoutRepository::class
  singleOf(::ModernizedLoadRIRRepositoryImpl) bind ModernizedLoadRIRRepository::class
  singleOf(::BackupManagerImpl) bind BackupManager::class

//  singleOf(::ExportWorkoutProgramRepository)
//  singleOf(::ImportWorkoutProgramRepository)

  single { WorkoutDatastore(get()) } // Added WorkoutDatastore to Koin module

  singleOf(::ManageWorkoutUseCase)
  singleOf(::ManagePreviewWorkoutUseCase)
  singleOf(::ManageExercisesUseCase)
//  singleOf(::ImportWorkoutProgram)

  viewModel { MainViewModel(get(), get(), get()) }
  viewModel { ProgramsViewModel(get(), get(), get(), get()) }
  viewModel { ExercisesViewModel(get(), get(), get()) }
  viewModel {
    WorkoutViewModel(
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
      get()
    )
  } // Added WorkoutDatastore to WorkoutViewModel definition
  viewModel { EquateVolumeViewModel(get(), get()) }
  viewModel { WorkingSetViewModel(get(), get()) }
  viewModel { WarmUpSetsViewModel(get(), get()) }
  viewModel { HistoryViewModel(get(), get(), get(), get(), get(), get()) }
  viewModel { ProfileViewModel(get(), get(), get(), get()) }
  viewModel { ReplicatorViewModel(get(), get()) }
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
