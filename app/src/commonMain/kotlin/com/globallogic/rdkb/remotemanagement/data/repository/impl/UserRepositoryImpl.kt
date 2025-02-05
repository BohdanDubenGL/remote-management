package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class UserRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val userDataSource: UserDataSource,
) : UserRepository {
    override suspend fun currentLoggedInUser(): User {
        val currentUserEmail = appPreferences.currentUserEmailPref.get() ?: return User.empty
        return userDataSource.findUserByEmail(currentUserEmail) ?: User.empty
    }

    override suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): User {
        val currentUserEmail = appPreferences.currentUserEmailPref.get() ?: return User.empty
        userDataSource.updateUser(currentUserEmail, settingsData.email, "", settingsData.password) ?: User.empty
        return currentLoggedInUser()
    }

    override suspend fun isEmailUsed(email: String): Boolean {
        return userDataSource.isEmailUsed(email)
    }

    override suspend fun register(registrationData: RegistrationData): User {
        val user = userDataSource.addUser(registrationData.email, "", registrationData.passwordHash) ?: return User.empty

        appPreferences.currentUserEmailPref.set(user.email)
        return user
    }

    override suspend fun login(loginData: LoginData): User {
        val user = userDataSource.findUserByCredentials(loginData.email, loginData.passwordHash) ?: return User.empty

        appPreferences.currentUserEmailPref.set(user.email)
        return user
    }

    override suspend fun logout(): Boolean {
        appPreferences.currentUserEmailPref.reset()
        return true
    }
}
