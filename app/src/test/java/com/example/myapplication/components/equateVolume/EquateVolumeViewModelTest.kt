package com.example.myapplication.components.equateVolume
/*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.data.model.Set
import com.example.myapplication.utils.newID
import java.util.UUID
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(AndroidJUnit4::class)
class EquateVolumeViewModelTest {

  private val equateVolumeViewModel: EquateVolumeViewModel = EquateVolumeViewModel()

  private fun initSets(): List<Set> {
    return List(3) { index ->
      Set(
        setNumber = index,
        exerciseWeight = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
        exerciseID = newID(),
        exerciseUUID = UUID.randomUUID(),
        workoutID = newID(),
        workoutUUID = UUID.randomUUID(),
        progressionSchemeID = 1,
      )
    }
  }

  */
/**
 * This test checks whether addSet() adds a new SetModel to the _sets value, with the correct
 * properties depending on whether _sets was initially empty or not.
 *//*

  @Test
  fun `addSet should add a new SetModel to _sets`() {
    //        equateVolumeViewModel = EquateVolumeViewModel()

    equateVolumeViewModel.changeWorkingSets(initSets())

    // Arrange
    val initialSets = equateVolumeViewModel.sets.value
    val initialSize = initialSets.size
    val expectedSize = initialSize + 1
    val expectedTotalVolume = 100.0 * 10 * expectedSize

    // Act
    equateVolumeViewModel.addSet()
    val newSets = equateVolumeViewModel.sets.value

    // Assert
    assertNotNull(initialSets)
    assertNotNull(newSets)
    assertEquals(expectedSize, newSets.size)

    if (initialSets.isNotEmpty()) {
      val lastSet = initialSets.last()
      val newSet = newSets.last()
      assertEquals(lastSet.setNumber?.plus(1), newSet.setNumber)
      assertNotEquals(lastSet.uuid, newSet.uuid)
      assertFalse(newSet.setCompleted)
    } else {
      assertEquals(1, newSets.size)
      assertNotNull(newSets[0].lazyListItemID)
      assertEquals(0, newSets[0].setNumber)
      assertFalse(newSets[0].setCompleted)
      assertEquals(expectedTotalVolume, equateVolumeViewModel.newTotalVolume.value)
    }
  }

  @Test
  fun `addSet should add a new SetModel to _sets when it is empty`() {
    //        equateVolumeViewModel = EquateVolumeViewModel()

    equateVolumeViewModel.changeWorkingSets(emptyList<Set>())

    // Arrange
    val initialSets = equateVolumeViewModel.sets.value
    val initialSize = initialSets.size
    val expectedSize = initialSize + 1

    // Act
    equateVolumeViewModel.addSet()
    val newSets = equateVolumeViewModel.sets.value

    // Assert
    assertNotNull(initialSets)
    assertNotNull(newSets)
    assertEquals(expectedSize, newSets.size)

    if (initialSets.isNotEmpty()) {
      val lastSet = initialSets.last()
      val newSet = newSets.last()
      assertEquals(lastSet.setNumber?.plus(1), newSet.setNumber)
      assertFalse(newSet.setCompleted)
    } else {
      assertEquals(1, newSets.size)
      assertNotNull(newSets[0].lazyListItemID)
      assertNotNull(newSets[0].uuid)
      assertEquals(0, newSets[0].setNumber)
      assertFalse(newSets[0].setCompleted)
    }
  }

  @Test
  fun `removeSet should remove the selected set from _sets`() {
    //        equateVolumeViewModel = EquateVolumeViewModel()

    equateVolumeViewModel.changeWorkingSets(initSets())

    // Arrange
    val initialSets = equateVolumeViewModel.sets.value
    val initialSize = initialSets.size
    val expectedSize = initialSize - 1
    val id = initialSets[1].lazyListItemID

    // Act
    equateVolumeViewModel.removeSet(id)

    // Assert
    assertEquals(expectedSize, equateVolumeViewModel.sets.value.size)
    assertNull(equateVolumeViewModel.sets.value.firstOrNull { it.lazyListItemID == id })
  }

  @Test
  fun `setTotalVolume sets the total volume`() {
    //        equateVolumeViewModel = EquateVolumeViewModel()

    // Given
    val totalVolume = 1000.0

    // When
    equateVolumeViewModel.setTotalVolume(totalVolume)

    // Then
    assertEquals(totalVolume, equateVolumeViewModel.totalVolume, 0.0)
  }

  @Test
  fun `test changeWorkingSetWeight updates oneRepMax`() {
    // Given a view model with an initial one rep max of 100
    val newWorkingWeight = 80.0

    // When changeWorkingSetWeight is called with a new weight of 200
    equateVolumeViewModel.changeWorkingSetWeight(newWorkingWeight)

    // Then the oneRepMax should be updated to 200
    assertEquals(newWorkingWeight, equateVolumeViewModel.oneRepMax.value, 0.0)
  }

  @Test
  fun testUpdateSetWeight() {
    // Create a set with a given weight
    val uuid = UUID.randomUUID()
    val initialWeight = 100.0
    val set =
      Set(
        uuid = uuid,
        exerciseWeight = initialWeight,
        repetitions = 10,
        exerciseID = newID(),
        exerciseUUID = UUID.randomUUID(),
        workoutID = newID(),
        workoutUUID = UUID.randomUUID(),
        progressionSchemeID = 1,
      )
    equateVolumeViewModel.changeWorkingSets(listOf(set))

    // Check that the set's weight was updated
    equateVolumeViewModel.updateSetWeight(uuid, initialWeight)
    var updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(initialWeight, updatedSet?.exerciseWeight)

    // Update the set's weight to a new value
    val newWeight = 150.0
    equateVolumeViewModel.updateSetWeight(uuid, newWeight)

    // Check that the set's weight was updated to the new value
    updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(newWeight, updatedSet?.exerciseWeight)
  }

  @Test
  fun testUpdateSetRepetitions() {
    // Create a set with a given number of repetitions
    val uuid = UUID.randomUUID()
    val initialReps = 10
    val set =
      Set(
        uuid = uuid,
        exerciseWeight = 100.0,
        repetitions = initialReps,
        exerciseID = newID(),
        exerciseUUID = UUID.randomUUID(),
        workoutID = newID(),
        workoutUUID = UUID.randomUUID(),
        progressionSchemeID = 1,
      )
    equateVolumeViewModel.changeWorkingSets(listOf(set))

    // Check that the set's number of repetitions was updated
    equateVolumeViewModel.updateSetReps(uuid, initialReps)
    var updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(initialReps, updatedSet?.repetitions)

    // Update the set's number of repetitions to a new value
    val newReps = 12
    equateVolumeViewModel.updateSetReps(uuid, newReps)

    // Check that the set's number of repetitions was updated to the new value
    updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(newReps, updatedSet?.repetitions)
  }

  @Test
  fun testUpdateSetPercentage() {
    // Create a set with a given percentage of 1RM
    val uuid = UUID.randomUUID()
    val initialPercentage = 0.75
    val set =
      Set(
        uuid = uuid,
        exerciseWeight = 100.0,
        repetitions = 10,
        percentage1RM = initialPercentage,
        exerciseID = newID(),
        exerciseUUID = UUID.randomUUID(),
        workoutID = newID(),
        workoutUUID = UUID.randomUUID(),
        progressionSchemeID = 1,
      )
    equateVolumeViewModel.changeWorkingSets(listOf(set))

    equateVolumeViewModel.updateSetPercentage(uuid, initialPercentage)

    // Check that the set's percentage of 1RM was updated
    var percentages = equateVolumeViewModel.setsPercentage.value
    assertEquals(initialPercentage, percentages[uuid])

    var updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(initialPercentage, updatedSet?.percentage1RM)

    // Update the set's percentage of 1RM to a new value
    val newPercentage = 0.80
    equateVolumeViewModel.updateSetPercentage(uuid, newPercentage)

    // Check that the set's percentage of 1RM was updated to the new value
    percentages = equateVolumeViewModel.setsPercentage.value
    assertEquals(newPercentage, percentages[uuid])

    updatedSet = equateVolumeViewModel.sets.value.find { it.uuid == uuid }
    assertEquals(newPercentage, updatedSet?.percentage1RM)
  }

  //    This test covers four cases: when newWeight is null, zero, positive with working sets, and
  // positive without working sets.
  //    It checks that the sets are updated as expected and that the total volume is recalculated
  // correctly.
  @Test
  fun `when newWeight is null, new repetitions & total volume should be zero`() {
    // Given
    equateVolumeViewModel.changeWorkingSets(initSets())
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByWeight(null)

    // Then
    assertEquals(0.0, equateVolumeViewModel.newTotalVolume.value, 0.0)
    assertEquals(0, equateVolumeViewModel.sets.value[1].repetitions)
  }

  @Test
  fun `when newWeight is zero, new repetitions & total volume should be zero`() {
    // Given
    equateVolumeViewModel.initialize(initSets())
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByWeight(0.0)

    // Then
    assertEquals(0.0, equateVolumeViewModel.newTotalVolume.value, 0.0)
    assertEquals(0, equateVolumeViewModel.sets.value[1].repetitions)
  }

  @Test
  fun `when newWeight is positive and there are working sets, sets should be updated`() {
    // Given
    equateVolumeViewModel.initialize(initSets())
    val oldSets = equateVolumeViewModel.sets.value
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByWeight(50.0)

    // Then
    val newSets = equateVolumeViewModel.sets.value
    val newTotalVolume = equateVolumeViewModel.newTotalVolume.value
    assertEquals(oldSets.size, newSets.size)
    assertEquals(oldTotalVolume, newTotalVolume, 0.0)

    val numberOfWorkingSets = oldSets.filter { it.setType.name != "WARM_UP_SET" }.size
    val expectedRepetitions = (oldTotalVolume / 50.0 / numberOfWorkingSets).toInt()

    newSets.forEach {
      it.exerciseWeight?.let { it1 -> assertEquals(it1, 50.0, 0.0) }
      assertEquals(it.repetitions, expectedRepetitions)
    }
  }

  @Test
  fun `when newWeight is positive and there are no working sets, sets should not be updated`() {
    // Given
    equateVolumeViewModel.initialize(emptyList())
    val oldSets = equateVolumeViewModel.sets.value
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByWeight(50.0)

    // Then
    assertEquals(oldSets, equateVolumeViewModel.sets.value)
    assertEquals(oldTotalVolume, equateVolumeViewModel.newTotalVolume.value, 0.0)
  }

  //    This test covers four cases: when newRepetitions is null, zero, positive with working sets,
  // and positive without working sets.
  //    It checks that the sets are updated as expected and that the total volume is recalculated
  // correctly.
  @Test
  fun `when newRepetitions is null, new repetitions & total volume should be zero`() {
    // Given
    equateVolumeViewModel.changeWorkingSets(initSets())
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByRepetitions(null)

    // Then
    assertEquals(0.0, equateVolumeViewModel.newTotalVolume.value, 0.0)
    assertEquals(null, equateVolumeViewModel.sets.value[1].repetitions)
  }

  @Test
  fun `when newRepetitions is zero, new repetitions & total volume should be zero`() {
    // Given
    equateVolumeViewModel.initialize(initSets())
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByRepetitions(0)

    // Then
    assertEquals(0.0, equateVolumeViewModel.newTotalVolume.value, 0.0)
    assertEquals(0.0, equateVolumeViewModel.sets.value[1].exerciseWeight)
  }

  @Test
  fun `when newRepetitions is positive and there are working sets, sets should be updated`() {
    // Given
    equateVolumeViewModel.initialize(initSets())
    val oldSets = equateVolumeViewModel.sets.value
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByRepetitions(50)

    // Then
    val newSets = equateVolumeViewModel.sets.value
    val newTotalVolume = equateVolumeViewModel.newTotalVolume.value
    assertEquals(oldSets.size, newSets.size)
    assertEquals(oldTotalVolume, newTotalVolume, 0.0)

    val numberOfWorkingSets = oldSets.filter { it.setType.name != "WARM_UP_SET" }.size
    val expectedWeight = (oldTotalVolume / 50 / numberOfWorkingSets)

    newSets.forEach {
      it.repetitions?.let { it1 -> assertEquals(it1, 50) }
      assertEquals(it.exerciseWeight, expectedWeight)
    }
  }

  @Test
  fun `when newRepetitions is positive and there are no working sets, sets should not be updated`() {
    // Given
    equateVolumeViewModel.initialize(emptyList())
    val oldSets = equateVolumeViewModel.sets.value
    val oldTotalVolume = equateVolumeViewModel.totalVolume

    // When
    equateVolumeViewModel.equateVolumeByRepetitions(50)

    // Then
    assertEquals(oldSets, equateVolumeViewModel.sets.value)
    assertEquals(oldTotalVolume, equateVolumeViewModel.newTotalVolume.value, 0.0)
  }
}
*/
