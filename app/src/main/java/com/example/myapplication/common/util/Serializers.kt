@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId
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