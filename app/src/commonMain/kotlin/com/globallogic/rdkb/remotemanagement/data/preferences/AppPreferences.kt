package com.globallogic.rdkb.remotemanagement.data.preferences

import kotlinx.coroutines.flow.Flow

interface AppPreferences{
    val currentUserEmailPref: Pref<String>
    val currentRouterDeviceMacAddressPref: Pref<String>
}

interface Pref<T: Any> {
    val prefFlow: Flow<T?>

    suspend fun get(): T?
    suspend fun get(default: T): T

    suspend fun set(value: T)
    suspend fun reset()
}
