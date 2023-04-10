package com.example.myapplication.components.equateVolume

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.data.SetModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(AndroidJUnit4::class)
class EquateVolumeViewModelTest {
    private lateinit var equateVolumeViewModel: EquateVolumeViewModel
    private fun initSets(): List<SetModel> {
        return List(3) { index ->
            SetModel(
                setNumber = index,
                exerciseWeight = 100.00,
                repetitions = 10,
                percentage1RM = 1.0
            )
        }
    }

    /**
     * This test checks whether addSet() adds a new SetModel to the _sets value, with the correct
     * properties depending on whether _sets was initially empty or not.
     * */
    @Test
    fun `addSet should add a new SetModel to _sets`() {
        equateVolumeViewModel = EquateVolumeViewModel(initSets())

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
            assertNotNull(newSets[0].id)
            assertEquals(0, newSets[0].setNumber)
            assertFalse(newSets[0].setCompleted)
        }
    }

    @Test
    fun `addSet should add a new SetModel to _sets when it is empty`() {
        equateVolumeViewModel = EquateVolumeViewModel(emptyList<SetModel>())

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
            assertNotNull(newSets[0].id)
            assertEquals(0, newSets[0].setNumber)
            assertFalse(newSets[0].setCompleted)
        }
    }
}
