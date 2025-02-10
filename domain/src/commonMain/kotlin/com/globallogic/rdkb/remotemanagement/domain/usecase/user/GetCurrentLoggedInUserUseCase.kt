package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetCurrentLoggedInUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<User, UserError.NoLoggedInUser> = getCurrentLoggedInUser()

    suspend fun getCurrentLoggedInUser(): Resource<User, UserError.NoLoggedInUser> {
        return userRepository.currentLoggedInUser()
    }
}
