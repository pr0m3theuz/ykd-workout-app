package com.example.myapplication.components.equateVolume

import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EquateVolumeViewModelTest {

    @Before
    fun setUp() {
    }

    /**
     * This test checks whether addSet() adds a new SetModel to the _sets value, with the correct
     * properties depending on whether _sets was initially empty or not.
     * */
    @Test
    fun `addSet should add a new SetModel to _sets value`() {
        // Arrange
        val viewModel = EquateVolumeViewModel()
        val initialSets = viewModel.sets.value
        val initialSize = initialSets.size ?: 0
        val expectedSize = initialSize + 1

        // Act
        viewModel.addSet()
        val newSets = viewModel.sets.value

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
            assertNull(newSets[0].id)
            assertNull(newSets[0].setNumber)
            assertFalse(newSets[0].setCompleted)
        }
    }
}
