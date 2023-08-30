package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model

import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.BaseRealmObject

sealed interface HasPrimaryKeyQuery<T : BaseRealmObject> {

    val thisObj: T

    fun createQueryForPrimaryKey(): Pair<String, Array<Any?>>

    fun queryDb(realm: Realm): RealmQuery<out T> {
        val query = createQueryForPrimaryKey()
        return realm.query(thisObj::class, query.first, *query.second)
    }

}