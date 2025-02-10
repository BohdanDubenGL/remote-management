package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.usecase.verification.EmailVerifier
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapErrorToData

class VerifyEmailForAuthenticationUseCase(
    private val userRepository: UserRepository,
    private val emailVerifier: EmailVerifier,
) {
    suspend operator fun invoke(email: String): Resource<EmailVerification, UserError.WrongEmailFormat> = verifyEmailForAuthentication(email)

    suspend fun verifyEmailForAuthentication(email: String): Resource<EmailVerification, UserError.WrongEmailFormat> = buildResource {
        if (!emailVerifier.verifyEmailFormat(email))
            return failure(UserError.WrongEmailFormat)

        return userRepository.getUserByEmail(email)
            .map { user -> EmailVerification.EmailIsUsed(email, user.username) }
            .mapErrorToData { error ->
                val nameSuggestion = email.split("@").firstOrNull() ?: email
                EmailVerification.EmailIsFree(email, nameSuggestion)
            }
    }
}

sealed interface EmailVerification {
    data class EmailIsUsed(val email: String, val name: String) : EmailVerification
    data class EmailIsFree(val email: String, val nameSuggestion: String) : EmailVerification
}
