package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.serializer

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.InsultRating
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

object InsultRatingSerializer : KSerializer<InsultRating> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("insult_rating") {
        element<Float>("rating")
        element<String>("explanation")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): InsultRating {
        return decoder.decodeStructure(descriptor) {
            var rating: Float? = null
            var explanation: String? = null
            while (true) {
                when (decodeElementIndex(descriptor)) {
                    0 -> rating = decodeFloatElement(descriptor, 0)
                    1 -> explanation = decodeStringElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            if (rating == null) {
                throw MissingFieldException("One of more required fields aren't present", descriptor.serialName)
            }
            return@decodeStructure InsultRating(
                rating, explanation
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: InsultRating) {
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.rating)
            encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.explanation)
        }
    }
}