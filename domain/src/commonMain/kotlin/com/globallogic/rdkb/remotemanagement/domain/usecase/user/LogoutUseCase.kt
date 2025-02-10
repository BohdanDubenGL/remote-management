package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit, UserError.LogoutError> = logout()

    suspend fun logout(): Resource<Unit, UserError.LogoutError> {
        return userRepository.logout()
    }
}
