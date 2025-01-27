package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.domain.usecase.login.LoginUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.login.LogoutUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.login.RegistrationUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.FactoryResetRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceConnectedDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceInfoUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceTopologyDataUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RestartRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SetupRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.ConnectToRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.GetRouterDeviceListUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.SearchRouterDevicesUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule: Module = module {
    factoryOf(::LoginUseCase)
    factoryOf(::RegistrationUseCase)
    factoryOf(::LogoutUseCase)

    factoryOf(::FactoryResetRouterDeviceUseCase)
    factoryOf(::GetRouterDeviceConnectedDevicesUseCase)
    factoryOf(::GetRouterDeviceInfoUseCase)
    factoryOf(::GetRouterDeviceTopologyDataUseCase)
    factoryOf(::RestartRouterDeviceUseCase)
    factoryOf(::SetupRouterDeviceUseCase)

    factoryOf(::ConnectToRouterDeviceUseCase)
    factoryOf(::GetRouterDeviceListUseCase)
    factoryOf(::SearchRouterDevicesUseCase)
}
