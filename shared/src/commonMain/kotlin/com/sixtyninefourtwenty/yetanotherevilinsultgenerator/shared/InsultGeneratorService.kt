package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.serializer.InsultSerializer
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

interface InsultGeneratorService {
    suspend fun generate(language: Language): Insult
}

abstract class OnlineInsultGeneratorService : InsultGeneratorService {

    protected abstract val url: String

    protected abstract val extraQueryParams: Map<String, String>

    private val client = HttpClient(getHttpClientEngineFactory())

    override suspend fun generate(language: Language): Insult {
        val response = client.get(url) {
            url {
                parameters.append("lang", language.languageCode)
                for ((name, value) in extraQueryParams) {
                    parameters.append(name, value)
                }
            }
        }
        return Json.decodeFromString(InsultSerializer, response.bodyAsText())
    }

}

class OfficialOnlineInsultGeneratorService(url: Url) : OnlineInsultGeneratorService() {

    override val url: String = url.url

    override val extraQueryParams: Map<String, String> = mapOf("type" to "json")

    enum class Url(internal val url: String) {
        MAIN(OFFICIAL_EIG_URL), BACKUP(OFFICIAL_EIG_BACKUP_URL)
    }
}

const val OFFICIAL_EIG_URL = "https://evilinsult.com/generate_insult.php"

const val OFFICIAL_EIG_BACKUP_URL = "https://slave.evilinsult.com/generate_insult.php"