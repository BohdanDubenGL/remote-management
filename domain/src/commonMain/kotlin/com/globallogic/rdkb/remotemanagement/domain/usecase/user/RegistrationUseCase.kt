package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.UserNameVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.verification.EmailVerifier

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
        val passwordErrors = passwordVerifier.verifyConfirmPassword(registrationData.password, registrationData.confirmPassword)
        if (passwordErrors.isNotEmpty()) {
            val userError = when (val error = passwordErrors.first()) {
                is PasswordVerifier.Error.EmptyPassword -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.WrongLength -> UserError.WrongPasswordLength(error.min, error.max)
                is PasswordVerifier.Error.DigitsRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.UppercaseRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.LowercaseRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.SpecialRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.NotMatch -> UserError.ConfirmPasswordDoesntMatch
            }
            return Failure(userError)
        }

        return userRepository.register(registrationData)
    }
}
