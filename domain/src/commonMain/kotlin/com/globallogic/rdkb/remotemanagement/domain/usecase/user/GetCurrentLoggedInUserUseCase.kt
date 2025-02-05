package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class GetCurrentLoggedInUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User?> = getCurrentLoggedInUser()

    suspend fun getCurrentLoggedInUser(): Result<User?> = userRepository.currentLoggedInUser()
}
