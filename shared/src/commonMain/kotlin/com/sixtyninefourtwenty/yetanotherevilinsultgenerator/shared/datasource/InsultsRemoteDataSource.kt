package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.InsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language

class InsultsRemoteDataSource(private val service: InsultGeneratorService) {

    suspend fun generateRemoteInsult(language: Language) = service.generate(language)

}