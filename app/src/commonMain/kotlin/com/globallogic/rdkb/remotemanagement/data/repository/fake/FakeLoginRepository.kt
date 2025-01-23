package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository

class FakeLoginRepository : LoginRepository {
    override suspend fun register(registrationData: RegistrationData): User = User.empty
    override suspend fun login(loginData: LoginData): User = User.empty
    override suspend fun logout() = Unit
}
