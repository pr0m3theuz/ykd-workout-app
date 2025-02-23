/*package com.example.myapplication.utils



object ObjectIdSerializer : KSerializer<ObjectId> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("org.bson.types.ObjectId", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ObjectId) {
    val string = value.toString()
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): ObjectId {
    val string = decoder.decodeString()
    return ObjectId(string)
  }
}*/
