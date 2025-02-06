package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> = logout()

    suspend fun logout(): Result<Boolean> = userRepository.logout()
}
