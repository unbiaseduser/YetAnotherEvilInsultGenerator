package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared

import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDateTime

expect fun getHttpClientEngineFactory(): HttpClientEngineFactory<*>

expect fun createLocalDateTimeFromInsultJsonField(str: String): LocalDateTime

expect fun createInsultJsonFieldFromLocalDateTime(dateTime: LocalDateTime): String
