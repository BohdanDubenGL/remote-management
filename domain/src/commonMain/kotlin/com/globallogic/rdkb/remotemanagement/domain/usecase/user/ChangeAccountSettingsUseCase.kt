package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.EmailVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.verification.UserNameVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class ChangeAccountSettingsUseCase(
    private val userRepository: UserRepository,
    private val userNameVerifier: UserNameVerifier,
    private val emailVerifier: EmailVerifier,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(settingsData: ChangeAccountSettingsData): Resource<Unit, UserError.ChangeAccountSettingsError> = changeAccountSettings(settingsData)

    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Resource<Unit, UserError.ChangeAccountSettingsError> = buildResource {
        if (!userNameVerifier.verifyUserNameLength(settingsData.username))
            return failure(UserError.WrongUsernameLength(userNameVerifier.minLength, userNameVerifier.maxLength))
        if (!userNameVerifier.verifyUserNameFormat(settingsData.username))
            return failure(UserError.WrongUsernameFormat)
        if (!emailVerifier.verifyEmailFormat(settingsData.email))
            return failure(UserError.WrongEmailFormat)
        if (!passwordVerifier.verifyPasswordLength(settingsData.password))
            return failure(UserError.WrongPasswordLength(passwordVerifier.minLength, passwordVerifier.maxLength))
        if (!passwordVerifier.verifyPasswordFormat(settingsData.password))
            return failure(UserError.WrongPasswordFormat)
        if (!passwordVerifier.verifyConfirmPassword(settingsData.password, settingsData.confirmPassword))
            return failure(UserError.ConfirmPasswordDoesntMatch)

        return userRepository.changeAccountSettings(settingsData).map { Unit }
    }
}
