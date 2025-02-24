package com.globallogic.rdkb.remotemanagement.domain.verification

class PasswordVerifier(
    val minLength: Int = 5,
    val maxLength: Int = 20,
    private val requiredDigits: Int = 0,
    private val requiredUppercase: Int = 0,
    private val requiredLowercase: Int = 0,
    private val requiredSpecial: Int = 0,
) {
    fun verifyPassword(password: String): List<Error> = buildList {
        if (password.isBlank()) {
            add(Error.EmptyPassword)
            return@buildList
        }
        if (password.length !in minLength..maxLength) add(Error.WrongLength(minLength, maxLength))
        if (password.count { it.isDigit() } >= requiredDigits) add(Error.DigitsRequired(requiredDigits))
        if (password.count { it.isUpperCase() } >= requiredUppercase) add(Error.UppercaseRequired(requiredUppercase))
        if (password.count { it.isLowerCase() } >= requiredLowercase) add(Error.LowercaseRequired(requiredUppercase))
        if (password.count { it in specialCharacters } >= requiredSpecial) add(Error.SpecialRequired(requiredUppercase))
    }

    fun verifyConfirmPassword(password: String, confirmPassword: String): List<Error> = buildList {
        addAll(verifyPassword(password))
        if (password != confirmPassword) add(Error.NotMatch)
    }

    sealed interface Error {
        data object EmptyPassword : Error
        data class WrongLength(val min: Int, val max: Int) : Error
        data class DigitsRequired(val count: Int) : Error
        data class UppercaseRequired(val count: Int) : Error
        data class LowercaseRequired(val count: Int) : Error
        data class SpecialRequired(val count: Int) : Error
        data object NotMatch : Error
    }

    companion object {
        private const val specialCharacters: String = "!@#\$%^&*()-_=+{}[]:;\"'<>,.?/"
    }
}
