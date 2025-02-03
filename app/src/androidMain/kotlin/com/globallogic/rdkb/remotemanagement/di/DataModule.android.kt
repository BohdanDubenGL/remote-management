package com.globallogic.rdkb.remotemanagement.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.globallogic.rdkb.remotemanagement.data.preferences.createDataStore
import com.globallogic.rdkb.remotemanagement.data.preferences.dataStoreFileName
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { createDataStore(androidContext(), dataStoreFileName) }.bind<DataStore<Preferences>>()
}
