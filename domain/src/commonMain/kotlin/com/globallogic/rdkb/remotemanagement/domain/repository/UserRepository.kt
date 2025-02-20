package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface UserRepository {
    suspend fun currentLoggedInUser(): Resource<User, UserError.NoLoggedInUser>
    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Resource<User, UserError.ChangeAccountSettingsError>

    suspend fun getUserByEmail(email: String): Resource<User, UserError.UserNotFound>
    suspend fun register(registrationData: RegistrationData): Resource<User, UserError.RegistrationError>
    suspend fun login(loginData: LoginData): Resource<User, UserError.LoginError>
    suspend fun logout(): Resource<Unit, UserError.LogoutError>
}
