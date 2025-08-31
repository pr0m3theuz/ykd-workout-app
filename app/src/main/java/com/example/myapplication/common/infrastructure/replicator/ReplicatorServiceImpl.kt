package com.example.myapplication.common.infrastructure.replicator

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorActivityLevel
import com.couchbase.lite.ReplicatorChange
import com.couchbase.lite.ReplicatorConfigurationFactory
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.newConfig
import com.couchbase.lite.replicatorChangesFlow
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI

private const val DATABASE_IS_NULL = "ReplicatorServiceDb: Database does not exist."

@InternalCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class ReplicatorServiceImpl(
//  private val authenticationService: AuthenticationService,
  private val userProfileManager: UserProfileManager,
  private val databaseManager: DatabaseManager,
) : ReplicatorService {
  private var replicatorManager: ReplicatorManager? = null

  //  private var loggedInUser = authenticationService.getCurrentUser()
  private var loggedInUser = userProfileManager.userProfile.value

  init {
    Log.i("ReplicatorServiceImpl", "init ReplicatorService: $this")
    Log.i(
      "ReplicatorServiceImpl",
      "init loggedInUser: $loggedInUser\nUserProfileManager: $userProfileManager\nReplicatorService: $this"
    )
  }

  // track replication state
  override var isReplicationStarted = false

  // if your sync gateway server is running on a different IP change it here
  override var replicationConfig =
    mutableStateOf(
      ReplicatorConfig(
        username = loggedInUser.username ?: "user",
        password = loggedInUser.password ?: "password",
        endpointUrl = loggedInUser.endpointUrl
          ?: "wss://127.0.0.1:4984/workouts", // TODO store in user profile
        replicatorType = "PUSH AND PULL",
        heartBeat = 60L,
        continuous = true,
        selfSignedCert = true,
      )
    )
  private var _replicationStatus = MutableStateFlow("")

  override val replicationStatus
    get() = _replicationStatus.asStateFlow()

  override val replicationTypes = listOf("PUSH AND PULL", "PUSH", "PULL")

  private var _canStartReplication = MutableStateFlow(false)

  override val canStartReplication
    get() = _canStartReplication.asStateFlow()

  override fun updateAuthentication(isReset: Boolean) {
    Log.i("ReplicatorServiceImpl.updateAuthentication", "updateReplicationConfig() called")
    Log.i(
      "ReplicatorServiceImpl.updateAuthentication",
      "ReplicatorServiceImpl.updateAuthentication(): loggedInUser: $loggedInUser\nReplicatorService: $this"
    )
//    loggedInUser = userProfileManager.getCurrentUser()
    if (isReset) {
      replicationConfig.value.username = ""
      replicationConfig.value.password = ""
    } else {
      replicationConfig.value.username = loggedInUser.username ?: "user"
      replicationConfig.value.password = loggedInUser.password ?: "password"
      Log.i(
        "ReplicatorServiceImpl.updateAuthentication",
        "databaseManager.database == null: ${databaseManager.database == null}"
      )
      databaseManager.database?.let { replicatorManager = ReplicatorManager(it) }
    }
  }

  override fun updateReplicationConfig(replicationConfig: ReplicatorConfig) {
    Log.i("updateReplicationConfig()", "called")
    updateAuthentication(false)
    if (
      replicatorManager?.replicatorConfiguration == null ||
      (replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.STOPPED ||
              replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.IDLE ||
              replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.OFFLINE)
    ) {
      replicatorManager?.let { replicatorResources ->
        val urlEndPoint = URLEndpoint(URI(replicationConfig.endpointUrl))

        _canStartReplication.value = true

        val replicationType = when (replicationConfig.replicatorType) {
          "PULL" -> ReplicatorType.PULL
          "PUSH" -> ReplicatorType.PUSH
          else -> ReplicatorType.PUSH_AND_PULL
        }

        // For Basic Authentication - https://docs.couchbase.com/couchbase-lite/current/android/replication.html#basic-authentication
        // For Session Authentication - https://docs.couchbase.com/couchbase-lite/current/android/replication.html#session-authentication
        // OIDC Authorization -
        //    https://www.couchbase.com/blog/oauth-2-oidc-fundamentals-authentication-authorization/
        //    https://www.couchbase.com/blog/oidc-implicit-flow-client-authentication-couchbase-sync-gateway/
        //    https://www.couchbase.com/blog/oidc-authorization-code-flow-client-authentication-couchbase-sync-gateway/
        val authenticator =
          BasicAuthenticator(replicationConfig.username, replicationConfig.password.toCharArray())

        requireNotNull(databaseManager.database) { DATABASE_IS_NULL }

        Log.i("Database Collections", databaseManager.database!!.collections.toString())

        val collections = mapOf(databaseManager.database!!.collections to null)

//        val collections = mapOf(
//          databaseManager.database!!.getCollection("Equipment") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Exercise") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("ExerciseNote") to CollectionConfiguration(),
////        databaseManager.database!!.getCollection("ExerciseSets") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Force") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Mechanic") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Mesocycle") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("MesocycleNote") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Microcycle") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("MicrocycleNote") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Muscle") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("MuscleExercise") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("MuscleGroup") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("MuscleRoleFunction") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Program") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("ProgramNote") to CollectionConfiguration(),
////        databaseManager.database!!.getCollection("Set") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Users") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Utility") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("Workout") to CollectionConfiguration(),
//          databaseManager.database!!.getCollection("WorkoutNote") to CollectionConfiguration()
//        )

//        Log.i("Collections Map", collections.toString())

        replicatorResources.replicatorConfiguration = ReplicatorConfigurationFactory.newConfig(
          target = urlEndPoint,
          collections = collections,
          type = replicationType,
          continuous = replicationConfig.continuous,
          authenticator = authenticator
        )

//        val collectionsList = listOf(
//          "Equipment",
//          "Exercise",
//          "ExerciseNote",
////        "ExerciseSets",
//          "Force",
//          "Mechanic",
//          "Mesocycle",
//          "MesocycleNote",
//          "Microcycle",
//          "MicrocycleNote",
//          "Muscle",
//          "MuscleExercise",
//          "MuscleGroup",
//          "MuscleRoleFunction",
//          "Program",
//          "ProgramNote",
////        "Set",
//          "Users",
//          "Utility",
//          "Workout",
//          "WorkoutNote"
//        )

//        collectionsList.forEach { collectionName ->
//          val collection = databaseManager.database!!.getCollection(collectionName)
//
//          collection?.let {
//          replicatorResources.replicatorConfiguration!!.addCollection(it, null)
//
//          }
//        }

        replicatorResources.replicatorConfiguration?.let { _ ->
          replicatorResources.replicator = Replicator(
            ReplicatorConfigurationFactory.newConfig(
              target = urlEndPoint,
              collections = collections,
              type = replicationType,
              continuous = replicationConfig.continuous,
              authenticator = authenticator,
//            headers = , // default is null
//            pinnedServerCertificate = , // default is null
//            maxAttempts = , // Default is 10
//            maxAttemptWaitTime = , // default is 300
//            heartbeat = replicationConfig.heartBeat.toInt() // Default is 300
//            acceptOnlySelfSignedServerCertificate = true // not used at all
//            enableAutoPurge = (), // Default is true
//            acceptParentDomainCookies = () // default is false
            )
          )
        }

        this.replicationConfig.value = replicationConfig // TODO Confirm if this works
      }
    } else {
      throw Exception("Error: can't update Replicator Config because replication is running")
    }
  }

  override fun calculateReplicationStatus() {
    if (replicatorManager?.replicatorConfiguration == null) {
      _replicationStatus.value = "${ReplicationStatus.NOCONFIG}, use Cog toolbar icon to set config"
    } else if (replicatorManager?.replicator == null) {
      _replicationStatus.value =
        "${ReplicationStatus.UNINITIALIZED}, use Cog toolbar icon to set config and replicator.  THIS SHOULD NEVER BE THE STATE"
    } else {
      replicatorManager?.replicator?.let {
        when (it.status.activityLevel) {
          ReplicatorActivityLevel.OFFLINE -> _replicationStatus.value = ReplicationStatus.OFFLINE
          ReplicatorActivityLevel.IDLE -> _replicationStatus.value = ReplicationStatus.IDlE
          ReplicatorActivityLevel.STOPPED -> _replicationStatus.value = ReplicationStatus.STOPPED
          ReplicatorActivityLevel.BUSY -> _replicationStatus.value = ReplicationStatus.BUSY
          ReplicatorActivityLevel.CONNECTING ->
            _replicationStatus.value = ReplicationStatus.CONNECTING
        }
      }
    }
  }

  override fun updateReplicationStatus(status: String) {
    _replicationStatus.value = status
  }

  override fun startReplication() {
    try {
      if (databaseManager.database == null) {
        databaseManager.initializeDatabases(/*authenticationService.getCurrentUser()*/)
      }
      replicatorManager?.replicator?.start()
      isReplicationStarted = true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun stopReplication() {
    try {
      replicatorManager?.replicator?.stop()
      isReplicationStarted = false
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun getReplicatorChangeFlow(): Flow<ReplicatorChange>? {
    replicatorManager?.replicator?.let {
      return it.replicatorChangesFlow()
    }
    return null
  }
}

object ReplicationStatus {
  const val STOPPED = "Stopped"
  const val OFFLINE = "Offline"
  const val IDlE = "Idle"
  const val BUSY = "Busy"
  const val CONNECTING = "Connecting"
  const val UNINITIALIZED = "Not Initialized"
  const val NOCONFIG = "No Replication Configuration"
}
