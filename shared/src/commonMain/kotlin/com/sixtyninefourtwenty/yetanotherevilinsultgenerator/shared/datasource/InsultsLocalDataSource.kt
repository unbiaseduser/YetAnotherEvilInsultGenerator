package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfflineInsultsDatabase
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult

class InsultsLocalDataSource(private val db: OfflineInsultsDatabase) {
    val allAvailableInsults = db.allAvailableInsults
    suspend fun addOrUpdateInsult(insult: Insult) = db.upsertObj(insult)
    suspend fun deleteInsult(insult: Insult) = db.deleteObj(insult)
    suspend fun deleteInsults(insults: Iterable<Insult>) = db.deleteObjs(insults)
    suspend fun updateFavoriteStatus(insult: Insult, isFavorite: Boolean) = db.updateFavoriteStatus(insult, isFavorite)
    fun getFavoriteStatus(insult: Insult) = db.getFavoriteStatus(insult)
}