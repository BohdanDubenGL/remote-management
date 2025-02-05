package com.globallogic.rdkb.remotemanagement.data.preferences.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.data.preferences.Pref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>
): AppPreferences {
    override val currentUserEmailPref: Pref<String> = dataStore.pref(keyCurrentUserEmail)
    override val currentRouterDeviceMacAddressPref: Pref<String> = dataStore.pref(keyCurrentRouterDeviceMacAddress)

    companion object {
        private val keyCurrentUserEmail: Preferences.Key<String> = stringPreferencesKey("current.user.email")
        private val keyCurrentRouterDeviceMacAddress: Preferences.Key<String> = stringPreferencesKey("current.routerDevice.macAddress")
    }
}


private fun <T: Any> DataStore<Preferences>.pref(prefKey: Preferences.Key<T>): Pref<T> = PrefImpl(this, prefKey)

private class PrefImpl<T: Any>(
    private val dataStore: DataStore<Preferences>,
    private val prefKey: Preferences.Key<T>
): Pref<T> {
    override val prefFlow: Flow<T?> = dataStore.data.map { it[prefKey] }

    override suspend fun get(): T? = prefFlow.firstOrNull()
    override suspend fun get(default: T): T = get() ?: default

    override suspend fun set(value: T) {
        dataStore.edit { it[prefKey] = value }
    }

    override suspend fun reset() {
        dataStore.edit { it.remove(prefKey) }
    }
}
