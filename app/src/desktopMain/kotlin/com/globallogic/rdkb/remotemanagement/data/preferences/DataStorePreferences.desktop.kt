package com.globallogic.rdkb.remotemanagement.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(fileName: String): DataStore<Preferences> = createDataStore {
    File(System.getProperty("user.home") + "/.remoteManagement", fileName).absolutePath
}
