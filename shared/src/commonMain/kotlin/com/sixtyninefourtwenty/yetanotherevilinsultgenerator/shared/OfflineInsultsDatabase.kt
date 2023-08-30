package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.HasPrimaryKeyQuery
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.InsultRating
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineInsultsDatabase(configuration: RealmConfiguration) {

    companion object {
        fun newInstance() = OfflineInsultsDatabase(RealmConfiguration.create(setOf(Insult::class, InsultRating::class)))
        fun newInMemoryInstance() = OfflineInsultsDatabase(RealmConfiguration.Builder(setOf(Insult::class, InsultRating::class))
            .inMemory()
            .build())
    }

    private val realm = Realm.open(configuration)

    val allAvailableInsults: Flow<List<Insult>> = realm.query<Insult>().asFlow().map { it.list }

    suspend fun <T : RealmObject> upsertObj(obj: T): T {
        return realm.write {
            copyToRealm(obj, UpdatePolicy.ALL)
        }
    }

    fun getFavoriteStatus(insult: Insult): Flow<Boolean> {
        return insult.queryDb(realm).asFlow().map { it.list.firstOrNull()?.isFavorite ?: throwInsultNotFoundByNumberException(insult) }
    }

    fun getRating(insult: Insult): Flow<InsultRating?> {
        return insult.queryDb(realm).asFlow().map {
            val insultInDb = it.list.firstOrNull() ?: throwInsultNotFoundByNumberException(insult)
            insultInDb.rating
        }
    }

    suspend fun updateFavoriteStatus(insult: Insult, isFavorite: Boolean) {
        updateObjProperties(insult) { it.isFavorite = isFavorite }
    }

    suspend fun updateRating(insult: Insult, rating: InsultRating) {
        updateObjProperties(insult) { it.rating = rating }
    }

    suspend fun <T> deleteObj(obj: T) where T : BaseRealmObject, T : HasPrimaryKeyQuery<T> {
        val objInDb = obj.queryDb(realm).first().find()
        if (objInDb is T) {
            realm.write {
                findLatest(objInDb)?.let(::delete)
            }
        }
    }

    suspend fun <T> deleteObjs(objs: Iterable<T>) where T : BaseRealmObject, T : HasPrimaryKeyQuery<T> {
        if (objs.any()) {
            realm.write {
                objs.forEach { obj ->
                    obj.queryDb(realm).find().forEach {
                        findLatest(it)?.let(::delete)
                    }
                }
            }
        }
    }

    private suspend fun <T> updateObjProperties(obj: T, block: (T) -> Unit) where T : BaseRealmObject, T : HasPrimaryKeyQuery<T> {
        val objInDb = obj.queryDb(realm).first().find()
        if (objInDb is T) {
            realm.write {
                findLatest(objInDb)?.let(block)
            }
        }
    }

    private fun throwInsultNotFoundByNumberException(insult: Insult): Nothing =
        throw IllegalArgumentException("Insult with number ${insult.number} doesn't exist in db")

    fun close() {
        realm.close()
    }

}