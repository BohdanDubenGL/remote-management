package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.fake.fake
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.LocalRouterDeviceDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.RemoteRouterDeviceDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.RouterDeviceConnectionDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.UserDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.db.AppDatabase
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.network.RdkCentralHttpClient
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralApiServiceImpl
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.data.preferences.impl.AppPreferencesImpl
import com.globallogic.rdkb.remotemanagement.data.repository.impl.RouterDeviceConnectionRepositoryImpl
import com.globallogic.rdkb.remotemanagement.data.repository.impl.RouterDeviceRepositoryImpl
import com.globallogic.rdkb.remotemanagement.data.repository.impl.UserRepositoryImpl
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule: Module = module {
    singleOf(::UserRepositoryImpl).bind<UserRepository>()
    singleOf(::RouterDeviceConnectionRepositoryImpl).bind<RouterDeviceConnectionRepository>()
    singleOf(::RouterDeviceRepositoryImpl).bind<RouterDeviceRepository>()

    singleOf(::UserDataSourceImpl).bind<UserDataSource>()
    singleOf(::LocalRouterDeviceDataSourceImpl).bind<LocalRouterDeviceDataSource>()
    single { RemoteRouterDeviceDataSourceImpl(get()).fake() }.bind<RemoteRouterDeviceDataSource>() //fake
    single { RouterDeviceConnectionDataSourceImpl().fake() }.bind<RouterDeviceConnectionDataSource>() //fake

    singleOf(::RdkCentralHttpClient).bind<HttpClient>()
    singleOf(::RdkCentralApiServiceImpl).bind<RdkCentralApiService>()

    singleOf(::AppPreferencesImpl).bind<AppPreferences>()

    singleOf(AppDatabase::getUserDao).bind<UserDao>()
    singleOf(AppDatabase::getRouterDeviceDao).bind<RouterDeviceDao>()
}
