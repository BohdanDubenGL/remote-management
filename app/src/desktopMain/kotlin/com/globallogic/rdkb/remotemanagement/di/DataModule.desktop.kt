package com.globallogic.rdkb.remotemanagement.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.globallogic.rdkb.remotemanagement.data.db.AppDatabase
import com.globallogic.rdkb.remotemanagement.data.db.createDatabaseBuilder
import com.globallogic.rdkb.remotemanagement.data.db.createRoomDatabase
import com.globallogic.rdkb.remotemanagement.data.preferences.createDataStore
import com.globallogic.rdkb.remotemanagement.data.preferences.dataStoreFileName
import com.globallogic.rdkb.remotemanagement.data.upnp.UpnpService
import com.globallogic.rdkb.remotemanagement.data.upnp.impl.UpnpServiceImpl
import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.impl.fake.FakeWifiScannerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { createDataStore(dataStoreFileName) }.bind<DataStore<Preferences>>()
    single { createRoomDatabase(createDatabaseBuilder<AppDatabase>(AppDatabase.fileName)) }.bind<AppDatabase>()

    singleOf(::FakeWifiScannerImpl).bind<WifiScanner>()
    single { UpnpServiceImpl(get()) }.bind<UpnpService>()
}
