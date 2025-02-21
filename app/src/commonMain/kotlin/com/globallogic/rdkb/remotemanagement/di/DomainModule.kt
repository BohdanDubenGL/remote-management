package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.DoRouterDeviceActionUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LoginUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LogoutUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.RegistrationUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetLocalRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceConnectedDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetWifiSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SelectRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SetupRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.AddRouterDeviceManuallyUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.ConnectToRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.GetRouterDeviceListUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.SearchRouterDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.ChangeAccountSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.VerifyEmailForAuthenticationUseCase
import com.globallogic.rdkb.remotemanagement.domain.verification.EmailVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.UserNameVerifier
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule: Module = module {
    factoryOf(::LoginUseCase)
    factoryOf(::RegistrationUseCase)
    factoryOf(::VerifyEmailForAuthenticationUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::GetCurrentLoggedInUserUseCase)
    factoryOf(::ChangeAccountSettingsUseCase)

    factoryOf(::DoRouterDeviceActionUseCase)
    factoryOf(::GetRouterDeviceConnectedDevicesUseCase)
    factoryOf(::GetWifiSettingsUseCase)
    factoryOf(::SetupRouterDeviceUseCase)
    factoryOf(::SelectRouterDeviceUseCase)
    factoryOf(::GetSelectedRouterDeviceUseCase)
    factoryOf(::GetLocalRouterDeviceUseCase)

    factoryOf(::ConnectToRouterDeviceUseCase)
    factoryOf(::AddRouterDeviceManuallyUseCase)
    factoryOf(::GetRouterDeviceListUseCase)
    factoryOf(::SearchRouterDevicesUseCase)

    factory { UserNameVerifier() }.bind<UserNameVerifier>()
    factoryOf(::EmailVerifier).bind<EmailVerifier>()
    factory { PasswordVerifier() }.bind<PasswordVerifier>()
}
