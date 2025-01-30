package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class RegistrationUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(registrationData: RegistrationData): User = register(registrationData)

    suspend fun register(registrationData: RegistrationData): User = userRepository.register(registrationData)
}
