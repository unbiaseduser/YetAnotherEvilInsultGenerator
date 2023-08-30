package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalDateTime as JavaLocalDateTime

actual fun getHttpClientEngineFactory(): HttpClientEngineFactory<*> {
    return object : HttpClientEngineFactory<OkHttpConfig> {
        override fun create(block: OkHttpConfig.() -> Unit): HttpClientEngine {
            return OkHttp.create(block)
        }
    }
}

private const val INSULT_JSON_DATE_TIME_PATTERN = "uuuu-MM-dd HH:mm:ss"

private val INSULT_JSON_FIELD_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(INSULT_JSON_DATE_TIME_PATTERN, Locale.ENGLISH)

actual fun createLocalDateTimeFromInsultJsonField(str: String): LocalDateTime {
    return JavaLocalDateTime.parse(str, INSULT_JSON_FIELD_DATE_TIME_FORMATTER).toKotlinLocalDateTime()
}

actual fun createInsultJsonFieldFromLocalDateTime(dateTime: LocalDateTime): String {
    return INSULT_JSON_FIELD_DATE_TIME_FORMATTER.format(dateTime.toJavaLocalDateTime())
}
