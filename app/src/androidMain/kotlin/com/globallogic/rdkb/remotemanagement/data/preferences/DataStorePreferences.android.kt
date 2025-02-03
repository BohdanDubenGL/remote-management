package com.globallogic.rdkb.remotemanagement.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context, fileName: String): DataStore<Preferences> = createDataStore {
    context.filesDir.resolve(fileName).absolutePath
}
