package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User

interface UserRepository {
    suspend fun currentLoggedInUser(): Result<User?>
    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Result<User?>

    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun register(registrationData: RegistrationData): Result<User>
    suspend fun login(loginData: LoginData): Result<User?>
    suspend fun logout(): Result<Boolean>
}
