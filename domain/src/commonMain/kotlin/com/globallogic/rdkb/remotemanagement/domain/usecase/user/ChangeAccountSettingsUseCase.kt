package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.EmailVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.UserNameVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class ChangeAccountSettingsUseCase(
    private val userRepository: UserRepository,
    private val userNameVerifier: UserNameVerifier,
    private val emailVerifier: EmailVerifier,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(settingsData: ChangeAccountSettingsData): Resource<Unit, UserError.ChangeAccountSettingsError> = changeAccountSettings(settingsData)

    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Resource<Unit, UserError.ChangeAccountSettingsError> {
        if (!userNameVerifier.verifyUserNameLength(settingsData.username))
            return Failure(
                UserError.WrongUsernameLength(
                    userNameVerifier.minLength,
                    userNameVerifier.maxLength
                )
            )
        if (!userNameVerifier.verifyUserNameFormat(settingsData.username))
            return Failure(UserError.WrongUsernameFormat)
        val emailErrors = emailVerifier.verifyEmail(settingsData.email)
        if (emailErrors.isNotEmpty()) {
            val emailError = when (emailErrors.first()) {
                EmailVerifier.Error.EmptyEmail -> UserError.WrongEmailFormat
                EmailVerifier.Error.InvalidFormat -> UserError.WrongEmailFormat
            }
            return Failure(emailError)
        }
        val passwordErrors = passwordVerifier.verifyConfirmPassword(settingsData.password, settingsData.confirmPassword)
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

        return userRepository.changeAccountSettings(settingsData).map { Unit }
    }
}
