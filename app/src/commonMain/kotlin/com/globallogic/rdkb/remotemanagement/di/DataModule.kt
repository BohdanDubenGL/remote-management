package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.fake.FakeRouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.fake.FakeRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.UserDataSourceImpl
import com.globallogic.rdkb.remotemanagement.data.db.AppDatabase
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.RdkCentralHttpClient
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralApiServiceImpl
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.data.preferences.impl.AppPreferencesImpl
import com.globallogic.rdkb.remotemanagement.data.repository.fake.FakeRouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.data.repository.fake.FakeRouterDeviceRepository
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
    singleOf(::FakeRouterDeviceConnectionRepository).bind<RouterDeviceConnectionRepository>()
    singleOf(::FakeRouterDeviceRepository).bind<RouterDeviceRepository>()

    singleOf(::UserDataSourceImpl).bind<UserDataSource>()
    singleOf(::FakeRouterDeviceDataSource).bind<FakeRouterDeviceDataSource>()
    singleOf(::FakeRouterDeviceConnectionDataSource).bind<FakeRouterDeviceConnectionDataSource>()


    singleOf(::RdkCentralHttpClient).bind<HttpClient>()
    singleOf(::RdkCentralApiServiceImpl).bind<RdkCentralApiService>()

    singleOf(::AppPreferencesImpl).bind<AppPreferences>()

    singleOf(AppDatabase::getUserDao).bind<UserDao>()
    singleOf(AppDatabase::getRouterDeviceDao).bind<RouterDeviceDao>()
}
