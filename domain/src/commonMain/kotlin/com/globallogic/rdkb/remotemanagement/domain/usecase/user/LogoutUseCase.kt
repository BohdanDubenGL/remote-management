package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean = logout()

    suspend fun logout(): Boolean = userRepository.logout()
}
