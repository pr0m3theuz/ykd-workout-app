package com.example.myapplication.common.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MassUnitTests {

    @Test
    fun `fromString returns KILOGRAMS for kg`() {
        assertEquals(MassUnit.KILOGRAMS, MassUnit.fromString("kg"))
    }

    @Test
    fun `fromString returns KILOGRAMS for KG`() {
        assertEquals(MassUnit.KILOGRAMS, MassUnit.fromString("KG"))
    }

    @Test
    fun `fromString returns KILOGRAMS for kilograms`() {
        assertEquals(MassUnit.KILOGRAMS, MassUnit.fromString("kilograms"))
    }

    @Test
    fun `fromString returns KILOGRAMS for KILOGRAMS`() {
        assertEquals(MassUnit.KILOGRAMS, MassUnit.fromString("KILOGRAMS"))
    }

    @Test
    fun `fromString returns POUNDS for lbs`() {
        assertEquals(MassUnit.POUNDS, MassUnit.fromString("lbs"))
    }

    @Test
    fun `fromString returns POUNDS for LBS`() {
        assertEquals(MassUnit.POUNDS, MassUnit.fromString("LBS"))
    }

    @Test
    fun `fromString returns POUNDS for pounds`() {
        assertEquals(MassUnit.POUNDS, MassUnit.fromString("pounds"))
    }

    @Test
    fun `fromString returns POUNDS for POUNDS`() {
        assertEquals(MassUnit.POUNDS, MassUnit.fromString("POUNDS"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromString throws IllegalArgumentException for invalid unit`() {
        MassUnit.fromString("invalid")
    }

    @Test
    fun `abbreviation for KILOGRAMS is kg`() {
        assertEquals("kg", MassUnit.KILOGRAMS.abbreviation)
    }

    @Test
    fun `abbreviation for POUNDS is lbs`() {
        assertEquals("lbs", MassUnit.POUNDS.abbreviation)
    }

    // Tests for MassUnitSerializer
    @Test
    fun `MassUnitSerializer serializes KILOGRAMS to kg`() {
        val json = kotlinx.serialization.json.Json
        val serialized = json.encodeToString(MassUnitSerializer, MassUnit.KILOGRAMS)
        assertEquals("\"kg\"", serialized) // Encoded as JSON string
    }

    @Test
    fun `MassUnitSerializer serializes POUNDS to lbs`() {
        val json = kotlinx.serialization.json.Json
        val serialized = json.encodeToString(MassUnitSerializer, MassUnit.POUNDS)
        assertEquals("\"lbs\"", serialized) // Encoded as JSON string
    }

    @Test
    fun `MassUnitSerializer deserializes kg to KILOGRAMS`() {
        val json = kotlinx.serialization.json.Json
        val deserialized = json.decodeFromString(MassUnitSerializer, "\"kg\"")
        assertEquals(MassUnit.KILOGRAMS, deserialized)
    }

    @Test
    fun `MassUnitSerializer deserializes lbs to POUNDS`() {
        val json = kotlinx.serialization.json.Json
        val deserialized = json.decodeFromString(MassUnitSerializer, "\"lbs\"")
        assertEquals(MassUnit.POUNDS, deserialized)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `MassUnitSerializer throws IllegalArgumentException for invalid unit on deserialization`() {
        val json = kotlinx.serialization.json.Json
        json.decodeFromString(MassUnitSerializer, "\"invalid\"")
    }
}
