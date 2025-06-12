package com.example.myapplication.common.domain.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(with = MassUnitSerializer::class)
enum class MassUnit(val abbreviation: String) {
    KILOGRAMS("kg"),
    POUNDS("lbs");

    companion object {
        fun fromString(value: String): MassUnit {
            return values().firstOrNull { it.abbreviation.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown mass unit: $value")
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
object MassUnitSerializer : KSerializer<MassUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MassUnit", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MassUnit) {
        encoder.encodeString(value.abbreviation)
    }

    override fun deserialize(decoder: Decoder): MassUnit {
        return MassUnit.fromString(decoder.decodeString())
    }
}
