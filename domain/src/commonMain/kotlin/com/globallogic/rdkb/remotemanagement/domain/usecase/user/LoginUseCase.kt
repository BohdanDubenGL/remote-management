package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.verification.PasswordVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource

class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend operator fun invoke(loginData: LoginData): Resource<User, UserError.LoginError> = login(loginData)

    suspend fun login(loginData: LoginData): Resource<User, UserError.LoginError> = buildResource {
        if (!passwordVerifier.verifyPasswordLength(loginData.password))
            return failure(UserError.WrongPasswordLength(passwordVerifier.minLength, passwordVerifier.maxLength))
        if (!passwordVerifier.verifyPasswordFormat(loginData.password))
            return failure(UserError.WrongPasswordFormat)

        return userRepository.login(loginData)
    }
}
