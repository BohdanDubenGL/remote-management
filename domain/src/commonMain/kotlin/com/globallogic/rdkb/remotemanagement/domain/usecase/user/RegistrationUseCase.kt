package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.UserNameVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure

class RegistrationUseCase(
    private val userRepository: UserRepository,
    private val userNameVerifier: UserNameVerifier,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(registrationData: RegistrationData): Resource<User, UserError.RegistrationError> = register(registrationData)

    suspend fun register(registrationData: RegistrationData): Resource<User, UserError.RegistrationError> {
        if (!userNameVerifier.verifyUserNameLength(registrationData.username))
            return Failure(
                UserError.WrongUsernameLength(
                    userNameVerifier.minLength,
                    userNameVerifier.maxLength
                )
            )
        if (!userNameVerifier.verifyUserNameFormat(registrationData.username))
            return Failure(UserError.WrongUsernameFormat)
        if (!passwordVerifier.verifyPasswordLength(registrationData.password))
            return Failure(
                UserError.WrongPasswordLength(
                    passwordVerifier.minLength,
                    passwordVerifier.maxLength
                )
            )
        if (!passwordVerifier.verifyPasswordFormat(registrationData.password))
            return Failure(UserError.WrongPasswordFormat)
        if (!passwordVerifier.verifyConfirmPassword(
                registrationData.password,
                registrationData.confirmPassword
            )
        )
            return Failure(UserError.ConfirmPasswordDoesntMatch)

        return userRepository.register(registrationData)
    }
}
