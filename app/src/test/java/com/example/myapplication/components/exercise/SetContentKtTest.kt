package com.example.myapplication.components.exercise

import com.example.myapplication.components.checkDoubleInput
import com.example.myapplication.components.doubleFormatted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SetContentKtTest {
    @Test
    fun testCheckDoubleInput() {
        // Test input string with padding
        assertEquals(100.01, checkDoubleInput("00,000,100.01987"))

        // Test input string with valid positive value
        assertEquals(50.0, checkDoubleInput("50")) // passed

        // Test input string with valid positive value
        assertEquals(1.23, checkDoubleInput("1.23")) // passed

        // Test input string with valid negative value
        assertEquals(-1.23, checkDoubleInput("-1.23"))

        // Test input string with invalid value
        assertEquals(null, checkDoubleInput("invalid"))

        // Test empty input string
        assertEquals(null, checkDoubleInput(""))

        // Test input string with padding
        assertEquals(0.01, checkDoubleInput("000000000000.01"))
    }

    @Test
    fun testDoubleFormatted() {
        // Test input with no decimal places
        assertEquals("123", doubleFormatted(123.0))

        // Test input with one decimal place
        assertEquals("1.2", doubleFormatted(1.2))

        // Test input with multiple decimal places
        assertEquals("3.14", doubleFormatted(3.14))

        // Test input with negative value
        assertEquals("-2.5", doubleFormatted(-2.5))

        // Test input with null value
        assertEquals("", doubleFormatted(null))
    }
}
