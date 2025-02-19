package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoUserError
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import com.globallogic.rdkb.remotemanagement.domain.utils.onSuccess

class UserRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val userDataSource: UserDataSource,
) : UserRepository {

    override suspend fun currentLoggedInUser(): Resource<User, UserError.NoLoggedInUser> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(UserError.NoLoggedInUser)
        return userDataSource.findUserByEmail(email).mapError { error ->
            when (error) {
                is IoUserError.DatabaseError -> UserError.NoLoggedInUser
                IoUserError.UserNotFound -> UserError.NoLoggedInUser
            }
        }
    }

    override suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Resource<User, UserError.ChangeAccountSettingsError> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(UserError.UserNotFound)
        val user = userDataSource.findUserByCredentials(email, settingsData.currentPassword)
            .mapError { error ->
                when (error) {
                    is IoUserError.DatabaseError -> UserError.WrongCredentials
                    is IoUserError.UserNotFound -> UserError.WrongCredentials
                }
            }
            .dataOrElse { error -> return Failure(error) }
        userDataSource.updateUser(user.email, settingsData.email, settingsData.username, settingsData.password)
            .mapError { error ->
                when (error) {
                    is IoUserError.DatabaseError -> UserError.UserNotFound
                    is IoUserError.UserNotFound -> UserError.UserNotFound
                }
            }
            .dataOrElse { error -> return Failure(error) }
        return currentLoggedInUser()
            .mapError { error -> UserError.UserNotFound }
    }

    override suspend fun getUserByEmail(email: String): Resource<User, UserError.UserNotFound> {
        return userDataSource.findUserByEmail(email).mapError { error ->
            when (error) {
                is IoUserError.DatabaseError -> UserError.UserNotFound
                IoUserError.UserNotFound -> UserError.UserNotFound
            }
        }
    }

    override suspend fun register(registrationData: RegistrationData): Resource<User, UserError.RegistrationError> {
        return userDataSource.addUser(registrationData.email, registrationData.username, registrationData.password)
            .onSuccess { user -> appPreferences.currentUserEmailPref.set(user.email) }
            .mapError { error ->
                when (error) {
                    is IoUserError.DatabaseError -> UserError.UserAlreadyExist
                    IoUserError.UserAlreadyExist -> UserError.UserAlreadyExist
                }
            }
    }

    override suspend fun login(loginData: LoginData): Resource<User, UserError.LoginError> {
        return userDataSource.findUserByCredentials(loginData.email, loginData.password)
            .onSuccess { user -> appPreferences.currentUserEmailPref.set(user.email) }
            .mapError { error ->
                when (error) {
                    is IoUserError.DatabaseError -> UserError.WrongCredentials
                    IoUserError.UserNotFound -> UserError.WrongCredentials
                }
            }
    }

    override suspend fun logout(): Resource<Unit, UserError.LogoutError> {
        appPreferences.currentUserEmailPref.reset()
        return Success(Unit)
    }
}
