package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class UserRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val userDataSource: UserDataSource,
) : UserRepository {
    private suspend fun currentUserEmail(): Result<String?> = runCatchingSafe { appPreferences.currentUserEmailPref.get() }

    override suspend fun currentLoggedInUser(): Result<User?> {
        return currentUserEmail()
            .mapCatching { email ->
                when (email) {
                    null -> null
                    else -> userDataSource.findUserByEmail(email).getOrThrow()
                }
            }
    }

    override suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Result<User?> {
        currentUserEmail()
            .mapCatching { email -> email ?: error("No user") }
            .mapCatching { email -> userDataSource.updateUser(email, settingsData.email, "", settingsData.password) }
        return currentLoggedInUser()
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        return userDataSource.findUserByEmail(email)
    }

    override suspend fun register(registrationData: RegistrationData): Result<User> {
        return userDataSource.addUser(registrationData.email, "", registrationData.passwordHash)
            .onSuccess { user -> appPreferences.currentUserEmailPref.set(user.email) }
    }

    override suspend fun login(loginData: LoginData): Result<User?> {
        return userDataSource.findUserByCredentials(loginData.email, loginData.passwordHash)
            .onSuccess { user -> if (user != null) appPreferences.currentUserEmailPref.set(user.email) }
    }

    override suspend fun logout(): Result<Boolean> = runCatchingSafe {
        appPreferences.currentUserEmailPref.reset()
        true
    }
}
