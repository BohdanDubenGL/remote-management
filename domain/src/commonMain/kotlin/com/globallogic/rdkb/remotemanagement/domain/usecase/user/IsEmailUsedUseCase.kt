package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class IsEmailUsedUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> = isEmailUsed(email)

    suspend fun isEmailUsed(email: String): Result<Boolean> = userRepository.isEmailUsed(email)
}
