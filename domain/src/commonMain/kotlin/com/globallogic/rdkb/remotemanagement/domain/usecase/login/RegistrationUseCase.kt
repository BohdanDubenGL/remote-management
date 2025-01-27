package com.globallogic.rdkb.remotemanagement.domain.usecase.login

import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.repository.LoginRepository

class RegistrationUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(registrationData: RegistrationData): User = register(registrationData)

    suspend fun register(registrationData: RegistrationData): User = loginRepository.register(registrationData)
}
