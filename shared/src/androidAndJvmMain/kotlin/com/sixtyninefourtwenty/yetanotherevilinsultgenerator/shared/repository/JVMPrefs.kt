package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository

import java.util.*

actual fun <E : Enum<E>> createEnumCollection(obj: E): MutableCollection<E> = EnumSet.noneOf(obj.declaringJavaClass)