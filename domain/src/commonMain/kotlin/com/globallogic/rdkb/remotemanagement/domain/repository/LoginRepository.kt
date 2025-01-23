package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User

interface LoginRepository {
    suspend fun register(registrationData: RegistrationData): User = User.empty

    suspend fun login(loginData: LoginData): User = User.empty

    suspend fun logout(): Unit = Unit

    companion object {
        val empty: LoginRepository = object : LoginRepository { }
    }
}
