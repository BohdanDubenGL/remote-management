package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class VerifyEmailForAuthenticationUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<EmailVerification> = verifyEmailForAuthentication(email)

    suspend fun verifyEmailForAuthentication(email: String): Result<EmailVerification> {
        return userRepository.getUserByEmail(email)
            .mapCatching { user ->
                when(user) {
                    null -> {
                        val nameSuggestion = email.split("@").firstOrNull() ?: email
                        EmailVerification.EmailIsFree(email, nameSuggestion)
                    }
                    else -> EmailVerification.EmailIsUsed(email, user.username)
                }
            }
    }
}

sealed interface EmailVerification {
    data class EmailIsUsed(val email: String, val name: String) : EmailVerification
    data class EmailIsFree(val email: String, val nameSuggestion: String) : EmailVerification
}
