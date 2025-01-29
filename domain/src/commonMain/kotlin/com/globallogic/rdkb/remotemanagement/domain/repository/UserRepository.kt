package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User

interface UserRepository {
    suspend fun currentLoggedInUser(): User
    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): User

    suspend fun register(registrationData: RegistrationData): User
    suspend fun login(loginData: LoginData): User
    suspend fun logout(): Boolean
}
