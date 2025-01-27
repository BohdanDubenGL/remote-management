package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.data.repository.fake.FakeLoginRepository
import com.globallogic.rdkb.remotemanagement.data.repository.fake.FakeRouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.data.repository.fake.FakeRouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule: Module = module {
    singleOf({ -> FakeLoginRepository() }).bind<LoginRepository>()
    singleOf({ -> FakeRouterDeviceConnectionRepository() }).bind<RouterDeviceConnectionRepository>()
    singleOf({ -> FakeRouterDeviceRepository() }).bind<RouterDeviceRepository>()
}
