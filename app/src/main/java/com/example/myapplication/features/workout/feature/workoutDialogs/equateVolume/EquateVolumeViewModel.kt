@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.setVolume
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.math.RoundingMode
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class EquateVolumeViewModel(
  private val userProfileManager: UserProfileManager,
  private val modernizedLoadRIRRepository: ModernizedLoadRIRRepository
) : ViewModel() {
  private val WARM_UP_SET = SetType.warmUpSet

  //  TODO REWRITE THIS
//  val currentUser = userRepository.getCurrentUser()
//  val userDefaultWeightUnit: String =
//    userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
//      ?: MassUnit.POUNDS.abbreviation
  val currentUser = userProfileManager.userProfile.value

  val userDefaultWeightUnit: String = currentUser.defaultWeightUnit.abbreviation

  private var exerciseWeightUnit: String? = null

  //    TODO Implement getOneRepMax() Query
  private var _oneRepMax = MutableStateFlow(100.0)
  val oneRepMax: StateFlow<Double>
    get() = _oneRepMax

  private var _sets = mutableListOf<Set>()
  private val _setsFlow = MutableStateFlow(_sets.toList())
  val sets: StateFlow<List<Set>>
    get() = _setsFlow.asStateFlow()

  private var _setsPercentage = MutableStateFlow(_sets.associate { it.uuid to 1.0 })
  val setsPercentage: StateFlow<Map<Uuid, Double>>
    get() = _setsPercentage

  private var _totalVolume: Double = 0.0
  val totalVolume: Double
    get() = _totalVolume

  private var _newTotalVolume = MutableStateFlow(0.0)
  val newTotalVolume: StateFlow<Double>
    get() = _newTotalVolume

  private var _exerciseSets: ExerciseExerciseSets? = null

  private var _modernizedLoadRIRList: List<ModernizedLoadRIR> = listOf()
  private val _modernizedLoadRIRListFlow = MutableStateFlow(_modernizedLoadRIRList)
  val modernizedLoadRIRList: StateFlow<List<ModernizedLoadRIR>>
    get() = _modernizedLoadRIRListFlow.asStateFlow()

  private fun getExerciseSets(exerciseSets: ExerciseExerciseSets) {
    _exerciseSets = exerciseSets
    exerciseWeightUnit = exerciseSets.exercise.weightUnit
    _sets = if (exerciseSets.sets.isNotEmpty()) exerciseSets.sets.toMutableList() else initSets(
      exerciseSets
    )

    _setsFlow.value = _sets.toList()
  }

  private fun getAllModernizedLoadRIRData(repCapacity: RepCapacity = RepCapacity.Average) {
    viewModelScope.launch {
      modernizedLoadRIRRepository.getRepCapacityModernizedLoadRIR(repCapacity).collect {
        _modernizedLoadRIRList = it
        _modernizedLoadRIRListFlow.value = _modernizedLoadRIRList
      }
    }
  }

  fun initialize(exerciseSets: ExerciseExerciseSets) {
    getExerciseSets(exerciseSets)
    changeWorkingSets(exerciseSets.sets)
    setTotalVolume(calculateTotalVolume(exerciseSets.sets))
    getAllModernizedLoadRIRData(exerciseSets.exercise.repCapacity)
  }


  fun setTotalVolume(totalVolume: Double) {
    _totalVolume = totalVolume
  }

  private fun setNewTotalVolume(totalVolume: Double) {
    _newTotalVolume.value = totalVolume
  }

  private val weightUnit = exerciseWeightUnit ?: userDefaultWeightUnit

  fun getWorkoutSetWeight(set: Set): Double? {
    return if (weightUnit == MassUnit.POUNDS.abbreviation) {
      set.weightPounds
    } else {
      set.weightKilograms
    }
  }

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _oneRepMax.value = newWorkingWeight
  }

  fun changeWorkingSets(sets: List<Set>) {
    _sets = sets.filter { it.setTypeID != WARM_UP_SET }.toMutableList()
    _setsFlow.value = _sets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets))
  }

  fun addSet() {
    val temporarySets: MutableList<Set> = _sets.toMutableList()

    requireNotNull(_exerciseSets) { "ExerciseSets cannot be null." }

    if (_sets.isNotEmpty()) {
      temporarySets.add(
        Set(
          setNumber = _sets.last().setNumber?.plus(1),
          weightPounds = temporarySets.last().weightPounds,
          weightKilograms = temporarySets.last().weightKilograms,
          repetitions = temporarySets.last().repetitions,
          ratePerceivedEffort = temporarySets.last().ratePerceivedEffort,
          repetitionsInReserve = temporarySets.last().repetitionsInReserve,
          setTypeID = temporarySets.last().setTypeID,
          percentage1RM = temporarySets.last().percentage1RM,
          exerciseID = temporarySets.last().exerciseID,
          exerciseSetsID = temporarySets.last().exerciseSetsID,
          workoutID = temporarySets.last().workoutID,
          isPublic = false,
          ownerID = _exerciseSets!!.ownerID, // TODO currentUser.id,
        )
      )
    } else {
      temporarySets.add(
        Set(
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id,
          workoutID = _exerciseSets!!.workoutID,
          ownerID = _exerciseSets!!.ownerID, // TODO currentUser.id,
        )
      )
    }
    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
    _setsFlow.value = _sets.toList()
  }

  /*
  how would you improve this function?
  Here are some suggestions to improve the addSet() function:
  1. The function creates a new mutable list by converting the current list to mutable.
      Instead, it's better to directly create a new mutable list and copy all the elements from the current list.
  2. The function accesses _sets multiple times, which is not optimal.
      It's better to assign it to a local variable for better readability and performance.
  3. Instead of using an if-else statement to check if the _sets list is empty or not, we can use the lastOrNull() function to get the last item in the list if it exists, otherwise return null.
  4. The copy() function can be simplified by using named arguments instead of positional arguments.
   */

  /**
   * Adds a new set to the list of sets and updates the total volume. If the list is empty, a new
   * set is created. Otherwise, a new set is created as a copy of the last set in the list, with the
   * set number incremented by 1 and the completion status set to false. The new set is then added
   * to the list of sets and the total volume is recalculated.
   */
  /*  fun chatgptAddSet() {
      val currentSets = _sets.toMutableList()
      val lastSet = currentSets.lastOrNull()

      val newSet =
        lastSet?.copy(
          lazyListItemID = newID(),
          Uuid = Uuid.random(),
          setNumber = lastSet.setNumber?.plus(1),
  //        exerciseID = exerciseID,
  //        workoutID = workoutID
        )
          ?: Set().apply{
  //          exerciseID = exerciseID
  //          exerciseUUID = exerciseUUID
  //          workoutID = workoutID
  //          workoutUUID = workoutUUID

            // TODO Replace with progressionScheme.progressionSchemeID
          }

      currentSets.add(newSet)
      _sets = currentSets.toList()

      setNewTotalVolume(calculateTotalVolume(_sets))
    }*/

  fun removeSet(id: Long) {
    val temporarySets = _sets
    temporarySets.removeIf { it.lazyListItemID == id }

    temporarySets.forEach { it.lazyListItemID = newLazyListItemID() }

    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
    _setsFlow.value = _sets.toList()
  }

  fun updateSetWeight(uuid: Uuid, newWeight: Double?) {
    val temporarySets = _sets.toMutableList()
    temporarySets.forEach {
      if (it.uuid == uuid) {
        it.weightPounds = newWeight?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP)?.toDouble()
      }
    }
    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
    _setsFlow.value = _sets.toList()
  }

  fun updateSetReps(uuid: Uuid, newReps: Int) {
    val temporarySets = _sets.toMutableList()
    temporarySets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
  }

  fun updateSetRIR(uuid: Uuid, newRIR: Double?) {
    val temporarySets = _sets.toMutableList()
    temporarySets.forEach { if (it.uuid == uuid) it.repetitionsInReserve = newRIR }
    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
  }

  fun updateSetPercentage(uuid: Uuid, newPercentage: Double) {
    val temporaryMap = _setsPercentage.value.toMutableMap()
    temporaryMap[uuid] = newPercentage
    _setsPercentage.value = temporaryMap

    val temporarySets = _sets.toMutableList()
    temporarySets.forEach { if (it.uuid == uuid) it.percentage1RM = newPercentage }
    _sets = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets))
    _setsFlow.value = _sets.toList()
  }

  fun updateAllSets(modernizedLoadRIR: ModernizedLoadRIR) {
    // TODO Implement this
//    val temporarySets = _sets.toMutableList()
//    temporarySets.forEach {
//      it.repetitionsInReserve = modernizedLoadRIR.rir
//      it.percentage1RM = modernizedLoadRIR.relativeLoadPercent
//      it.ratePerceivedEffort = modernizedLoadRIR.rpe
//      it.repetitions = modernizedLoadRIR.repsPerformed
//    }
//    _sets = temporarySets
//    setNewTotalVolume(calculateTotalVolume(_sets))
  }

  fun equateVolumeByWeight(newWeight: Double?) {
    val numberOfWorkingSets: Int = numberOfWorkingSets()
    val newRepetitions =
      if (numberOfWorkingSets > 0 && newWeight != null && newWeight > 0.0) {
        totalVolume.div(newWeight).div(numberOfWorkingSets).roundToInt()
      } else {
        0
      }

    val newSets = _sets
    newSets.forEach {
      it.lazyListItemID = newLazyListItemID()
      it.weightPounds = newWeight
      it.repetitions = newRepetitions
    }

    _sets = newSets
    setNewTotalVolume(calculateTotalVolume(_sets))
    _setsFlow.value = _sets.toList()
  }

  fun equateVolumeByRepetitions(newRepetitions: Int?) {
    val numberOfWorkingSets: Int = numberOfWorkingSets()
    val newWeight =
      if (numberOfWorkingSets > 0 && newRepetitions != null && newRepetitions > 0) {
        totalVolume.div(numberOfWorkingSets).div(newRepetitions)
      } else {
        0.0
      }

    val newSets = sets.value
    newSets.forEach {
      it.lazyListItemID = newLazyListItemID()
      it.weightPounds = newWeight
      it.repetitions = newRepetitions
    }

    _sets = newSets.toMutableList()
    _setsFlow.value = _sets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets))
  }

  private fun numberOfWorkingSets(): Int {
    var numberOfWorkingSets: Int = 0

    _sets.filter { it.setTypeID != WARM_UP_SET }.forEach { _ -> numberOfWorkingSets++ }

    return numberOfWorkingSets
  }

  private fun calculateTotalVolume(temporarySets: List<Set>): Double {
    var totalVolume: Double = 0.0

    temporarySets
      .filter { it.setTypeID != WARM_UP_SET }
      .forEach { totalVolume += (it.setVolume(weightUnit)) }

    return totalVolume
  }

  private fun initSets(exerciseSets: ExerciseExerciseSets): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
        setNumber = index.plus(1),
        weightPounds = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
        exerciseID = exerciseSets.exercise.id,
        exerciseSetsID = exerciseSets.id,
        workoutID = exerciseSets.workoutID,
        ownerID = exerciseSets.ownerID, // TODO currentUser.id,
        //        exerciseID = exerciseID,
        //        exerciseUUID = exerciseUUID,
        //        workoutID = workoutID,
        //        workoutUUID = workoutUUID,
      )
    }
  }
}

