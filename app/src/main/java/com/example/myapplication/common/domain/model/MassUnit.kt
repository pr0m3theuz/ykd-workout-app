package com.example.myapplication.common.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MassUnitSerializer::class)
enum class MassUnit(val abbreviation: String, val displayText: String) {
  KILOGRAMS("kg", "Metric"),
  POUNDS("lbs", "Imperial");

  companion object {
    fun fromString(value: String?): MassUnit {
      return entries.firstOrNull {
        it.abbreviation.equals(
          value,
          ignoreCase = true
        ) || it.name.equals(value, ignoreCase = true)
      }
        ?: POUNDS // Default to Pounds if no match is found
    }
  }
}

@OptIn(ExperimentalSerializationApi::class)
object MassUnitSerializer : KSerializer<MassUnit> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("MassUnit", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: MassUnit) {
    encoder.encodeString(value.abbreviation)
  }

  override fun deserialize(decoder: Decoder): MassUnit {
    return MassUnit.fromString(decoder.decodeString())
  }
}
