package com.globallogic.rdkb.remotemanagement.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

inline fun initializeKoin(crossinline platformDeclaration: KoinAppDeclaration = { }) {
    startKoin {
        platformDeclaration()

        modules(
            domainModule,
            dataModule, platformDataModule,
            viewModule, platformViewModule
        )
    }
}
