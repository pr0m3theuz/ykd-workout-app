package com.example.myapplication.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

private const val DOUBLE_VALIDATION_REGEX = "^(-?\\d*)(\\.?)(\\d{0,2})(\\d*)\$"
private fun isDoubleValid(double: String): Boolean {
    return Pattern.matches(DOUBLE_VALIDATION_REGEX, double)
}

fun brokenCheckDoubleInput(it: String): String? {
    var double: String?

    var manipulatedInput = it.replace(",", "")

    manipulatedInput = if (manipulatedInput.count { it == '.' } <= 1) {
        val doublePattern = Pattern.compile(DOUBLE_VALIDATION_REGEX)
        val matcher = doublePattern.matcher(manipulatedInput)
        matcher.matches()
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        ""
    }

    if (isDoubleValid(manipulatedInput) && manipulatedInput.isNotBlank()) {
        double = if (manipulatedInput == ".") {
            "0." // Add leading zero
        } else if (manipulatedInput.last() == '.') {
            manipulatedInput // Prevent early removal of the decimal place
        } else {
            val paddedInput: String =
                NumberFormat.getInstance().parse(manipulatedInput)?.toDouble()
                    .toString() // = it.padStart(16, '0')

            if (paddedInput.toDouble() != 0.0) {
                NumberFormat.getInstance().parse(paddedInput)?.toString()
            } else {
                "0"
            }
        }
    } else {
        double = null
    }

    return double
}

private val DOUBLE_PATTERN = Pattern.compile(DOUBLE_VALIDATION_REGEX)

/**
 * Extracts a valid double-precision floating-point number from the given input string,
 * and returns the result as a string.
 *
 * @param input The input string to process. Must not be null.
 *
 * @return The double-precision floating-point number represented by the input string,
 *         or null if the input string is not a valid representation of a number.
 */
fun checkDoubleInput(input: String): String? {
    var manipulatedInput = input.replace(",", "")
    val matcher = DOUBLE_PATTERN.matcher(manipulatedInput)
    manipulatedInput = if (manipulatedInput.count { it == '.' } <= 1 && matcher.matches()) {
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        return null
    }

    return if (manipulatedInput.isNotBlank()) {
        when {
            manipulatedInput == "." -> "0." // Add leading zero
            manipulatedInput == "-" -> manipulatedInput
            manipulatedInput.last() == '.' -> manipulatedInput // Prevent early removal of the decimal place
            else -> {
                val paddedInput = NumberFormat.getInstance().parse(manipulatedInput)?.toString()
                val parsedDouble = paddedInput?.toDoubleOrNull() ?: return null
                if (parsedDouble == 0.0) "0" else paddedInput
            }
        }
    } else {
        null
    }
}

/**
 * Produced by OpenAI's ChatGPT
 * Prompt "can you optimize the below code written in kotlin?" in referring to the function above.
 * Extracts a valid double-precision floating-point number from the given input string,
 * and returns the result as a string.
 *
 * @param input The input string to process. Must not be null.
 *
 * @return The double-precision floating-point number represented by the input string,
 *         or null if the input string is not a valid representation of a number.
 */
fun chatgptCheckDoubleInput(input: String): String? {
    val inputWithoutCommas = input.replace(",", "")
    val matcher = DOUBLE_PATTERN.matcher(inputWithoutCommas)
    val formattedInput = if (matcher.matches()) {
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        return null
    }

    return when {
        formattedInput == "." -> "0."
        formattedInput.last() == '.' -> formattedInput
        else -> {
            val paddedInput = NumberFormat.getInstance().parse(formattedInput)?.toString()
            val parsedDouble = paddedInput?.toDoubleOrNull() ?: return null
            if (parsedDouble == 0.0) "0" else paddedInput
        }
    }
}

fun checkIntInput(input: String): String {
    return when {
        input.isBlank() -> ""
        input.contentEquals(".") -> ""
        input.contentEquals(",") -> ""
        input.contentEquals("-") -> ""
        else -> {
            val paddedInput = input.padStart(16, '0')
            NumberFormat.getInstance().parse(paddedInput)?.toInt().toString()
        }
    }
}

private val instance: NumberFormat = DecimalFormat.getInstance(Locale.getDefault())

/**
 * Formats a given double-precision floating-point number according to the following rules:
 *
 * - If the input is null, return an empty string.
 * - If the input has no decimal component (i.e. is an integer), return it as a string, rounded to the nearest integer.
 * - Otherwise, return the input as a string with its full decimal component.
 *
 * @param double The double-precision floating-point number to format. May be null.
 *
 * @return The formatted string representation of the input number.
 */
fun doubleFormatted(double: Double?): String {
    return when (double) {
        null -> ""
        double.roundToInt().toDouble() -> double.roundToInt().toString()
        else -> double.toString()
    }
}
