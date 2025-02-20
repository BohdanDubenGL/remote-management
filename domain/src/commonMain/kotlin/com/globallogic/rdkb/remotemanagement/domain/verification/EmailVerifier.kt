package com.globallogic.rdkb.remotemanagement.domain.verification

class EmailVerifier {
    private val emailRegex: Regex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+".toRegex()

    fun verifyEmailFormat(email: String): Boolean = emailRegex.matches(email)
}
