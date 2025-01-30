package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.view.screen.authentication.AuthenticationViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.connection.AddRouterDeviceManuallyViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.connection.SearchRouterDeviceViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.home.ChangeAccountSettingsViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.home.RouterDeviceListViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.home.SettingsViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.home.TopologyViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.ConnectedDeviceListViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterDeviceViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterSettingsViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.SetupRouterDeviceViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformViewModule: Module

val viewModule: Module = module {
    viewModelOf(::SplashViewModel)

    viewModelOf(::AuthenticationViewModel)

    viewModelOf(::SearchRouterDeviceViewModel)
    viewModelOf(::AddRouterDeviceManuallyViewModel)

    viewModelOf(::TopologyViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ChangeAccountSettingsViewModel)
    viewModelOf(::RouterDeviceListViewModel)

    viewModelOf(::RouterDeviceViewModel)
    viewModelOf(::ConnectedDeviceListViewModel)
    viewModelOf(::SetupRouterDeviceViewModel)
    viewModelOf(::RouterSettingsViewModel)
}
