package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.usecase.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.usecase.verification.UserNameVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource

class RegistrationUseCase(
    private val userRepository: UserRepository,
    private val userNameVerifier: UserNameVerifier,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(registrationData: RegistrationData): Resource<User, UserError.RegistrationError> = register(registrationData)

    suspend fun register(registrationData: RegistrationData): Resource<User, UserError.RegistrationError> = buildResource {
        if (!userNameVerifier.verifyUserNameLength(registrationData.username))
            return failure(UserError.WrongUsernameLength(userNameVerifier.minLength, userNameVerifier.maxLength))
        if (!userNameVerifier.verifyUserNameFormat(registrationData.username))
            return failure(UserError.WrongUsernameFormat)
        if (!passwordVerifier.verifyPasswordLength(registrationData.password))
            return failure(UserError.WrongPasswordLength(passwordVerifier.minLength, passwordVerifier.maxLength))
        if (!passwordVerifier.verifyPasswordFormat(registrationData.password))
            return failure(UserError.WrongPasswordFormat)
        if (!passwordVerifier.verifyConfirmPassword(registrationData.password, registrationData.confirmPassword))
            return failure(UserError.ConfirmPasswordDoesntMatch)

        return userRepository.register(registrationData)
    }
}
