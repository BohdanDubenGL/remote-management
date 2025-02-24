package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure

class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(loginData: LoginData): Resource<User, UserError.LoginError> = login(loginData)

    suspend fun login(loginData: LoginData): Resource<User, UserError.LoginError> {
        val passwordErrors = passwordVerifier.verifyPassword(loginData.password)
        if (passwordErrors.isNotEmpty()) {
            val userError = when (val error = passwordErrors.first()) {
                is PasswordVerifier.Error.EmptyPassword -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.WrongLength -> UserError.WrongPasswordLength(error.min, error.max)
                is PasswordVerifier.Error.DigitsRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.UppercaseRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.LowercaseRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.SpecialRequired -> UserError.WrongPasswordFormat
                is PasswordVerifier.Error.NotMatch -> null
            }
            if (userError != null) return Failure(userError)
        }

        return userRepository.login(loginData)
    }
}
