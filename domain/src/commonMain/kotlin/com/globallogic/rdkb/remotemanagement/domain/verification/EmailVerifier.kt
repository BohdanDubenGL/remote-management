package com.globallogic.rdkb.remotemanagement.domain.verification

class EmailVerifier(
    private val emailRegex: Regex = defaultEmailRegex
) {
    fun verifyEmail(email: String): List<Error> = buildList {
        if (email.isBlank()) {
            add(Error.EmptyEmail)
            return@buildList
        }
        if (!emailRegex.matches(email)) add(Error.InvalidFormat)
    }

    sealed interface Error {
        data object EmptyEmail : Error
        data object InvalidFormat : Error
    }

    companion object {
        private val defaultEmailRegex: Regex = "^(?!.*\\.\\.)(?!^[._%+\\-])(?!.*[._%+\\-]$)[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    }
}
