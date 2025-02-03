package com.globallogic.rdkb.remotemanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(fileName: String): DataStore<Preferences> = createDataStore {
    "./$fileName"
}
