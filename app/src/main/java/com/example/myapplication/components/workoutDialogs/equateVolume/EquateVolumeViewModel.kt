package com.example.myapplication.components.workoutDialogs.equateVolume

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.Set
import com.example.myapplication.data.setVolume
import com.example.myapplication.newID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.roundToInt

private const val WARM_UP_SET = 2

@HiltViewModel
class EquateVolumeViewModel @Inject constructor() : ViewModel() {
  //    TODO Implement getOneRepMax() Query
  private var _oneRepMax = MutableStateFlow(100.0)
  val oneRepMax: StateFlow<Double>
    get() = _oneRepMax

  private var _sets = MutableStateFlow(emptyList<Set>())
  val sets: StateFlow<List<Set>>
    get() = _sets

  private var _setsPercentage = MutableStateFlow(_sets.value.associate { it.uuid to 1.0 })
  val setsPercentage: StateFlow<Map<RealmUUID, Double>>
    get() = _setsPercentage

  private var _totalVolume: Double = 0.0
  public val totalVolume: Double
    get() = _totalVolume

  private var _newTotalVolume = MutableStateFlow(0.0)
  val newTotalVolume: StateFlow<Double>
    get() = _newTotalVolume

  private var _exerciseSets: ExerciseSets? = null

//  private var exerciseID: Long = 0
//  private var exerciseUUID: RealmUUID = RealmUUID.random()
//  private var workoutID: Long = 0
//  private var workoutUUID: RealmUUID = RealmUUID.random()

  private fun getExerciseSets(exerciseSets: ExerciseSets) {
    _exerciseSets = exerciseSets
//    exerciseID = exerciseSets.exercise.exerciseID
//    exerciseUUID = exerciseSets.exercise.uuid
//    workoutID = exerciseSets.workoutID
//    workoutUUID = exerciseSets.workoutUUID

    _sets.value = exerciseSets.sets.toList()
  }

  fun initialize(exerciseSets: ExerciseSets) {
    getExerciseSets(exerciseSets)
    changeWorkingSets(exerciseSets.sets)
    setTotalVolume(calculateTotalVolume(exerciseSets.sets))
  }

  fun setTotalVolume(totalVolume: Double) {
    _totalVolume = totalVolume
  }

  private fun setNewTotalVolume(totalVolume: Double) {
    _newTotalVolume.value = totalVolume
  }

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _oneRepMax.value = newWorkingWeight
  }

  fun changeWorkingSets(sets: List<Set>) {
    _sets.value = sets.filter { it.setTypeID != WARM_UP_SET }
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  fun addSet() {
    val temporarySets: MutableList<Set> = _sets.value.toMutableList()

    if (_sets.value.isNotEmpty()) {
      temporarySets.add(
        Set().apply {
          setNumber = _sets.value.last().setNumber?.plus(1)
          exerciseWeight = temporarySets.last().exerciseWeight
          repetitions = temporarySets.last().repetitions
          ratePerceivedEffort = temporarySets.last().ratePerceivedEffort
          repetitionsInReserve = temporarySets.last().repetitionsInReserve
          setTypeID = temporarySets.last().setTypeID
          percentage1RM = temporarySets.last().percentage1RM
        }
      )
    } else {
      temporarySets.add(Set())
    }
    _sets.value = temporarySets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  /*
  how would you improve this function?
  Here are some suggestions to improve the addSet() function:
  1. The function creates a new mutable list by converting the current list to mutable.
      Instead, it's better to directly create a new mutable list and copy all the elements from the current list.
  2. The function accesses _sets.value multiple times, which is not optimal.
      It's better to assign it to a local variable for better readability and performance.
  3. Instead of using an if-else statement to check if the _sets.value list is empty or not, we can use the lastOrNull() function to get the last item in the list if it exists, otherwise return null.
  4. The copy() function can be simplified by using named arguments instead of positional arguments.
   */

  /**
   * Adds a new set to the list of sets and updates the total volume. If the list is empty, a new
   * set is created. Otherwise, a new set is created as a copy of the last set in the list, with the
   * set number incremented by 1 and the completion status set to false. The new set is then added
   * to the list of sets and the total volume is recalculated.
   */
  /*  fun chatgptAddSet() {
      val currentSets = _sets.value.toMutableList()
      val lastSet = currentSets.lastOrNull()

      val newSet =
        lastSet?.copy(
          lazyListItemID = newID(),
          RealmUUID = RealmUUID.random(),
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
      _sets.value = currentSets.toList()

      setNewTotalVolume(calculateTotalVolume(_sets.value))
    }*/

  fun removeSet(id: Long) {
    val temporarySets = _sets.value.toMutableList()
    temporarySets.removeIf { it.lazyListItemID == id }

    temporarySets.forEach { it.lazyListItemID = newID() }

    _sets.value = temporarySets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  fun updateSetWeight(uuid: RealmUUID, newWeight: Double?) {
    val temporarySets = _sets.value.toMutableList()
    temporarySets.forEach {
      if (it.uuid == uuid) {
        it.exerciseWeight = newWeight?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP)?.toDouble()
      }
    }
    _sets.value = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  fun updateSetReps(uuid: RealmUUID, newReps: Int) {
    val temporarySets = _sets.value.toMutableList()
    temporarySets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
    _sets.value = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  fun updateSetPercentage(uuid: RealmUUID, newPercentage: Double) {
    val temporaryMap = _setsPercentage.value.toMutableMap()
    temporaryMap[uuid] = newPercentage
    _setsPercentage.value = temporaryMap

    val temporarySets = _sets.value.toMutableList()
    temporarySets.forEach { if (it.uuid == uuid) it.percentage1RM = newPercentage }
    _sets.value = temporarySets
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  fun equateVolumeByWeight(newWeight: Double?) {
    val numberOfWorkingSets: Int = numberOfWorkingSets()
    val newRepetitions =
      if (numberOfWorkingSets > 0 && newWeight != null && newWeight > 0.0) {
        totalVolume.div(newWeight).div(numberOfWorkingSets).roundToInt()
      } else {
        0
      }

    val newSets = _sets.value
    newSets.forEach {
      it.lazyListItemID = newID()
      it.exerciseWeight = newWeight
      it.repetitions = newRepetitions
    }

    _sets.value = newSets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets.value))
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
      it.lazyListItemID = newID()
      it.exerciseWeight = newWeight
      it.repetitions = newRepetitions
    }

    _sets.value = newSets.toList()
    setNewTotalVolume(calculateTotalVolume(_sets.value))
  }

  private fun numberOfWorkingSets(): Int {
    var numberOfWorkingSets: Int = 0

    _sets.value.filter { it.setTypeID != WARM_UP_SET }.forEach { _ -> numberOfWorkingSets++ }

    return numberOfWorkingSets
  }

  private fun calculateTotalVolume(temporarySets: List<Set>): Double {
    var totalVolume: Double = 0.0

    temporarySets.filter { it.setTypeID != WARM_UP_SET }.forEach { totalVolume += (it.setVolume()) }

    return totalVolume
  }

  /*  private fun initSets(): List<Set> {
      return List(3) { index ->
        Set(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10,
            percentage1RM = 1.0,
            exerciseID = exerciseID!!,
            exerciseUUID = exerciseUUID!!,
            workoutID = workoutID!!,
            workoutUUID = workoutUUID!!,
            progressionSchemeID =
            Random().nextInt(), // TODO Replace with progressionScheme.progressionSchemeID
        )
      }
    }*/
}
