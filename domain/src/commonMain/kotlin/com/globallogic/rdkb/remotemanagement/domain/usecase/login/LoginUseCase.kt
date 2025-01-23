package com.globallogic.rdkb.remotemanagement.domain.usecase.login

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository

class LoginUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(loginData: LoginData): User = login(loginData)

    suspend fun login(loginData: LoginData): User = loginRepository.login(loginData)
}
