@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.SetType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object ObjectIdSerializer : KSerializer<ObjectId> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("org.bson.types.ObjectId", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): ObjectId {
    return ObjectId(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: ObjectId) {
    encoder.encodeString(value.toHexString())
  }
}

object UuidSerializer : KSerializer<Uuid> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("app.Uuid", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Uuid {
    return Uuid.parseHex(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: Uuid) {
    encoder.encodeString(value.toHexString())
  }
}

object ProgressionTypeSerializer : KSerializer<ProgressionType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("com.example.MyApplication.ProgressionType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): ProgressionType {
    return ProgressionType.entries[(decoder.decodeInt())]
  }

  override fun serialize(encoder: Encoder, value: ProgressionType) {
    encoder.encodeInt(value.ordinal)
  }
}

object SetTypeSerializer : KSerializer<SetType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("com.example.MyApplication.ProgressionType", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): SetType {
    return SetType.getSetType(decoder.decodeInt())
  }

  override fun serialize(encoder: Encoder, value: SetType) {
    encoder.encodeInt(value.setTypeID)
  }
}

object InstantSerializer : KSerializer<Instant> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("kotlin.time.Instant", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Instant {
    return Instant.parse(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: Instant) {
    encoder.encodeString(value.toString())
  }
}