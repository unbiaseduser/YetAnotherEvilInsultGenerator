package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.serializer

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.InsultRating
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object InsultSerializer : KSerializer<Insult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("insult") {
        //Elements in json returned by EIG service
        element<String>("number")
        element<String>("language")
        element<String>("insult")
        element<String>("created")
        element<String>("shown")
        element<String>("createdby")
        element<String>("active")
        element<String>("comment")
        //App-specific properties, serialized inline to prevent having to query the db
        element<Boolean>("is_favorite", isOptional = true)
        element<InsultRating>("rating", isOptional = true)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Insult {
        return decoder.decodeStructure(descriptor) {
            var number: Int? = null
            var language: String? = null
            var insult: String? = null
            var created: String? = null
            var shown: Int? = null
            var createdBy: Any? = null
            var active: Int? = null
            var comment: Any? = null
            var isFavorite = false
            var rating: InsultRating? = null
            while (true) {
                when (decodeElementIndex(descriptor)) {
                    0 -> number = decodeStringElement(descriptor, 0).toInt()
                    1 -> language = decodeStringElement(descriptor, 1)
                    2 -> insult = decodeStringElement(descriptor, 2)
                    3 -> created = decodeStringElement(descriptor, 3)
                    4 -> shown = decodeStringElement(descriptor, 4).toInt()
                    5 -> createdBy = decodeStringElement(descriptor, 5).takeIf { it.isNotEmpty() }
                    6 -> active = decodeStringElement(descriptor, 6).toInt()
                    7 -> comment = decodeStringElement(descriptor, 7).takeIf { it.isNotEmpty() }
                    8 -> isFavorite = decodeBooleanElement(descriptor, 8)
                    9 -> rating = decodeNullableSerializableElement(descriptor, 9, InsultRatingSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            if (number == null ||
                language == null ||
                insult == null ||
                created == null ||
                shown == null ||
                createdBy !is String? ||
                active == null ||
                comment !is String?) {
                throw MissingFieldException("One of more required fields aren't present", descriptor.serialName)
            }
            return@decodeStructure Insult(
                number, language, insult, created, shown, createdBy, active, comment, isFavorite, rating
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Insult) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.number.toString())
            encodeStringElement(descriptor, 1, value.language)
            encodeStringElement(descriptor, 2, value.insult)
            encodeStringElement(descriptor, 3, value.created)
            encodeStringElement(descriptor, 4, value.shown.toString())
            encodeStringElement(descriptor, 5, value.createdBy.orEmpty())
            encodeStringElement(descriptor, 6, value.active.toString())
            encodeStringElement(descriptor, 7, value.comment.orEmpty())
            encodeBooleanElement(descriptor, 8, value.isFavorite)
            encodeNullableSerializableElement(descriptor, 9, InsultRatingSerializer, value.rating)
        }
    }

}