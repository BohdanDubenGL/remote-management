package com.globallogic.rdkb.remotemanagement.data.preferences.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences

class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>
): AppPreferences {

    companion object {

    }
}
