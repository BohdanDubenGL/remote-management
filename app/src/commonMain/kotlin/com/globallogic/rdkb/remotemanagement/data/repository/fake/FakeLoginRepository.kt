package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeLoginRepository
@OptIn(ExperimentalUuidApi::class) constructor(
    private val users: MutableMap<String, User> = mutableMapOf(
        "user@user.com" to User(Uuid.random(), "user", "user@user.com"),
    ),
    private var currentUser: User? = User(Uuid.random(), "user", "user@user.com")
) : LoginRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun register(registrationData: RegistrationData): User {
        if (registrationData.email in users) return User.empty
        val user = User(Uuid.random(), registrationData.username, registrationData.email)
        users[registrationData.email] = user
        return user
    }

    override suspend fun login(loginData: LoginData): User {
        return users.getOrElse(loginData.email) { User.empty }
    }

    override suspend fun logout() {
        currentUser = null
    }
}
