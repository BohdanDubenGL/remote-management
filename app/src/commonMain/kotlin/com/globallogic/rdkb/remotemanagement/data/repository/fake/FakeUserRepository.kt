package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeUserRepository
@OptIn(ExperimentalUuidApi::class) constructor(
    private val users: MutableMap<String, Pair<User, String>> = mutableMapOf(
        "user@user.com" to (User(Uuid.random(), "user", "user@user.com") to "password"),
    ),
    private var currentUser: User = User.empty
) : UserRepository {
    override suspend fun currentLoggedInUser(): User = currentUser

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): User {
        currentUser = User(currentUser.uuid, settingsData.email, settingsData.password)
        return currentUser
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun register(registrationData: RegistrationData): User {
        if (registrationData.email in users) return User.empty
        val user = User(Uuid.random(), registrationData.username, registrationData.email)
        users[registrationData.email] = user to registrationData.passwordHash
        currentUser = user
        return user
    }

    override suspend fun login(loginData: LoginData): User {
        val (user, password) = users.getOrElse(loginData.email) { User.empty to "" }
        if (password != loginData.passwordHash) return User.empty
        currentUser = user
        return user
    }

    override suspend fun logout(): Boolean {
        currentUser = User.empty
        return true
    }
}
