package com.globallogic.rdkb.remotemanagement.domain.usecase.login

import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository

class LogoutUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(): Unit = logout()

    suspend fun logout(): Unit = loginRepository.logout()
}
