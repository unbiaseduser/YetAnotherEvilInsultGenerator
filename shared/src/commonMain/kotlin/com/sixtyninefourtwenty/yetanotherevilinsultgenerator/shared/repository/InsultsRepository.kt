package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsLocalDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsRemoteDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language

class InsultsRepository(
    private val remoteDataSource: InsultsRemoteDataSource,
    private val localDataSource: InsultsLocalDataSource
) {
    val allAvailableLocalInsults = localDataSource.allAvailableInsults
    suspend fun deleteLocalInsult(insult: Insult) = localDataSource.deleteInsult(insult)
    suspend fun deleteLocalInsults(insults: Iterable<Insult>) = localDataSource.deleteInsults(insults)
    suspend fun addOrUpdateLocalInsult(insult: Insult) = localDataSource.addOrUpdateInsult(insult)
    suspend fun updateFavoriteStatus(insult: Insult, isFavorite: Boolean) =
        localDataSource.updateFavoriteStatus(insult, isFavorite)
    fun getFavoriteStatus(insult: Insult) = localDataSource.getFavoriteStatus(insult)

    suspend fun generateRemoteInsult(language: Language) = remoteDataSource.generateRemoteInsult(language)
}