package com.example.myapplication.common.infrastructure.persistence.datastore

import androidx.datastore.core.CorruptionException
import com.example.myapplication.datastore.ActiveWorkout
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ActiveWorkoutSerializerTest {

    private val serializer = ActiveWorkoutSerializer

    @Test
    fun `defaultValue returns default ActiveWorkout instance`() {
        assertThat(serializer.defaultValue).isEqualTo(ActiveWorkout.getDefaultInstance())
    }

    @Test
    fun `readFrom reads valid data correctly`() = runBlocking {
        val expectedWorkout = ActiveWorkout.newBuilder()
            .setId("test-id")
            .setName("Test Workout")
            .setUuid("test-uuid")
            .setOwnerID("owner-test-id")
            .build()

        val outputStream = ByteArrayOutputStream()
        expectedWorkout.writeTo(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        val actualWorkout = serializer.readFrom(inputStream)

        assertThat(actualWorkout).isEqualTo(expectedWorkout)
    }

    @Test
    fun `writeTo writes data correctly`() = runBlocking {
        val workout = ActiveWorkout.newBuilder()
            .setId("test-id-2")
            .setName("Test Workout 2")
            .setUuid("test-uuid-2")
            .setOwnerID("owner-test-id-2")
            .build()

        val outputStream = ByteArrayOutputStream()
        serializer.writeTo(workout, outputStream)

        val parsedWorkout = ActiveWorkout.parseFrom(ByteArrayInputStream(outputStream.toByteArray()))

        assertThat(parsedWorkout).isEqualTo(workout)
    }

    @Test
    fun `readFrom throws CorruptionException for corrupted data`() = runBlocking {
        val corruptedData = "this is not a valid protobuf".toByteArray()
        val inputStream = ByteArrayInputStream(corruptedData)

        val exception = assertThrows(CorruptionException::class.java) {
            runBlocking { serializer.readFrom(inputStream) }
        }
        assertThat(exception).hasMessageThat().contains("Cannot read proto.")
    }
}
