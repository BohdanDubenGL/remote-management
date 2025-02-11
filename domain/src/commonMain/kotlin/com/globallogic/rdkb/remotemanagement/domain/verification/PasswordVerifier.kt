package com.globallogic.rdkb.remotemanagement.domain.verification

class PasswordVerifier(
    val minLength: Int = 5,
    val maxLength: Int = 20,
) {
    fun verifyPasswordLength(password: String): Boolean = password.length in minLength..maxLength

    fun verifyPasswordFormat(password: String): Boolean = password.none { it in " \n\r" }

    fun verifyConfirmPassword(password: String, confirmPassword: String): Boolean = password == confirmPassword
}
