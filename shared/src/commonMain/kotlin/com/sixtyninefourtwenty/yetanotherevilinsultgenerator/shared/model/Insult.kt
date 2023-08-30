package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.serializer.InsultSerializer
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.createLocalDateTimeFromInsultJsonField
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable(with = InsultSerializer::class)
class Insult() : RealmObject, HasPrimaryKeyQuery<Insult> {

    @PrimaryKey
    var number: Int = 0
    var language: String = Language.ENGLISH.languageCode
    var insult: String = ""
    var created: String = "1996-01-01 00:00:00"
    var shown: Int = 0
    var createdBy: String? = null
    var active: Int = 0
    var comment: String? = null
    var isFavorite: Boolean = false
    var rating: InsultRating? = null

    override val thisObj: Insult
        get() = this

    override fun createQueryForPrimaryKey(): Pair<String, Array<Any?>> {
        return Pair("number == $0", arrayOf(number))
    }

    fun getLanguage() = Language.values().first { it.languageCode == language }

    fun getCreatedDateTime() = createLocalDateTimeFromInsultJsonField(created)

    constructor(
        number: Int,
        language: String,
        insult: String,
        created: String,
        shown: Int,
        createdBy: String?,
        active: Int,
        comment: String?,
        isFavorite: Boolean = false,
        rating: InsultRating? = null
    ) : this() {
        this.number = number
        this.language = language
        this.insult = insult
        this.created = created
        this.shown = shown
        this.createdBy = createdBy
        this.active = active
        this.comment = comment
        this.isFavorite = isFavorite
        this.rating = rating
    }

    fun toJson() = Json.encodeToString(InsultSerializer, this)

    fun hasCreatedBy() = !createdBy.isNullOrBlank()

    fun hasComment() = !comment.isNullOrBlank()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Insult) return false
        if (number != other.number) return false
        if (language != other.language) return false
        if (insult != other.insult) return false
        if (created != other.created) return false
        if (shown != other.shown) return false
        if (createdBy != other.createdBy) return false
        if (active != other.active) return false
        if (comment != other.comment) return false
        if (isFavorite != other.isFavorite) return false
        return rating == other.rating
    }

    override fun hashCode(): Int {
        var result = number.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + insult.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + shown.hashCode()
        result = 31 * result + (createdBy?.hashCode() ?: 0)
        result = 31 * result + active.hashCode()
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + (rating?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun fromJson(json: String): Insult = Json.decodeFromString(InsultSerializer, json)
    }

}
