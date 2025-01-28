package com.globallogic.rdkb.remotemanagement.di

import com.globallogic.rdkb.remotemanagement.view.screen.authentication.AuthenticationViewModel
import com.globallogic.rdkb.remotemanagement.view.screen.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformViewModule: Module

val viewModule: Module = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::AuthenticationViewModel)
}
