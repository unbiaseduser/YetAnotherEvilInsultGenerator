package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.serializer.InsultRatingSerializer
import io.realm.kotlin.types.EmbeddedRealmObject
import kotlinx.serialization.Serializable

@Serializable(with = InsultRatingSerializer::class)
class InsultRating() : EmbeddedRealmObject {

    var rating: Float = 5.0F
    var explanation: String? = null

    constructor(
        rating: Float,
        explanation: String?
    ) : this() {
        this.rating = rating
        this.explanation = explanation
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InsultRating) return false
        if (rating != other.rating) return false
        return explanation == other.explanation
    }

    override fun hashCode(): Int {
        var result = rating.hashCode()
        result = 31 * result + explanation.hashCode()
        return result
    }


}