package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Unit = logout()

    suspend fun logout(): Unit = userRepository.logout()
}
