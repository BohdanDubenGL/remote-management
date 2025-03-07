package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.LocalUserDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.fake.fake
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.LocalRouterDeviceDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.RemoteRouterDeviceDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.LocalUserDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.db.AppDatabase
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.network.RdkCentralHttpClient
import com.globallogic.rdkb.remotemanagement.data.network.service.NetworkUpnpDeviceDataService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralNetworkApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.NetworkUpnpDeviceDataServiceImpl
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralNetworkApiServiceImpl
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralPropertyServiceImpl
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralAccessorServiceImpl
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

    singleOf(::LocalUserDataSourceImpl).bind<LocalUserDataSource>()
    singleOf(::LocalRouterDeviceDataSourceImpl).bind<LocalRouterDeviceDataSource>()
    singleOf(::RemoteRouterDeviceDataSourceImpl).bind<RemoteRouterDeviceDataSourceImpl>()
    single { get<RemoteRouterDeviceDataSourceImpl>().fake() }.bind<RemoteRouterDeviceDataSource>() //fake

    single { RdkCentralHttpClient() }.bind<HttpClient>()
    singleOf(::RdkCentralNetworkApiServiceImpl).bind<RdkCentralNetworkApiService>()
    singleOf(::RdkCentralPropertyServiceImpl).bind<RdkCentralPropertyService>()
    singleOf(::RdkCentralAccessorServiceImpl).bind<RdkCentralAccessorService>()
    singleOf(::NetworkUpnpDeviceDataServiceImpl).bind<NetworkUpnpDeviceDataService>()

    singleOf(::AppPreferencesImpl).bind<AppPreferences>()

    singleOf(AppDatabase::getUserDao).bind<UserDao>()
    singleOf(AppDatabase::getRouterDeviceDao).bind<RouterDeviceDao>()
}
