package com.example.myapplication.common

import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.checkIntInput
import com.example.myapplication.common.util.doubleFormatted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NumberUtilsTest {
    @Test
    fun testCheckDoubleInput() {
        // Test input string with negative value
        assertEquals(null, checkDoubleInput("--"))

        // Test input string with negative value
        assertEquals("-", checkDoubleInput("-"))

        // Test input string with valid negative value
        assertEquals(null, checkDoubleInput("1.23-"))

        // Test input string with valid positive value
        assertEquals(null, checkDoubleInput("950....")) // passed

        // Test only decimal point input
        assertEquals("0.", checkDoubleInput("."))

        // Test only decimal point input
        assertEquals("0.", checkDoubleInput("0."))

        // Test input string with padding
        assertEquals("100.01", checkDoubleInput("00,000,100.01987"))

        // Test input string with valid positive value
        assertEquals("50", checkDoubleInput("50")) // passed

        // Test input string with valid positive value
        assertEquals("50.", checkDoubleInput("50.")) // passed

        // Test input string with valid positive value
        assertEquals("1.23", checkDoubleInput("1.23")) // passed

        // Test input string with valid negative value
        assertEquals("-1.23", checkDoubleInput("-1.23"))

        // Test input string with invalid value
        assertEquals(null, checkDoubleInput("invalid"))

        // Test empty input string
        assertEquals(null, checkDoubleInput(""))

        // Test empty input string
        assertEquals("0.1", checkDoubleInput(".1"))

        // Test input string with padding
        assertEquals("0.01", checkDoubleInput("000000000000.01"))

        // Test comma 0 input string
        assertEquals("0", checkDoubleInput(",0"))

        // Test comma input string
        assertEquals(null, checkDoubleInput(","))
    }

    @Test
    fun testCheckIntInput() {
        // Test input string with negative value
        assertEquals("0", checkIntInput("--"))

        // Test input string with negative value
        assertEquals("", checkIntInput("-"))

        // Test input string with valid negative value
        assertEquals("1", checkIntInput("1.23-"))

        // Test input string with valid positive value
        assertEquals("950", checkIntInput("950....")) // passed

        // Test only decimal point input
        assertEquals("", checkIntInput("."))

        // Test only decimal point input
        assertEquals("0", checkIntInput("0."))

        // Test input string with padding
        assertEquals("100", checkIntInput("00,000,100.01987"))

        // Test input string with valid positive value
        assertEquals("50", checkIntInput("50")) // passed

        // Test input string with valid positive value
        assertEquals("50", checkIntInput("50.")) // passed

        // Test input string with valid positive value
        assertEquals("1", checkIntInput("1.23")) // passed

        // Test input string with valid negative value
        assertEquals("0", checkIntInput("-1.23"))

        // Test input string with invalid value
        assertEquals("0", checkIntInput("invalid"))

        // Test empty input string
        assertEquals("", checkIntInput(""))

        // Test empty input string
        assertEquals("0", checkIntInput(".1"))

        // Test input string with padding
        assertEquals("0", checkIntInput("000000000000.01"))

        // Test comma 0 input string
        assertEquals("0", checkIntInput(",0"))

        // Test comma input string
        assertEquals("", checkIntInput(","))
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
