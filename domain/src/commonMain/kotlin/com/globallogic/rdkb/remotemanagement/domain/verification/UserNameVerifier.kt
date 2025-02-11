package com.globallogic.rdkb.remotemanagement.domain.verification

class UserNameVerifier(
    val minLength: Int = 5,
    val maxLength: Int = 20,
) {
    fun verifyUserNameLength(username: String): Boolean = username.length in minLength .. maxLength
    fun verifyUserNameFormat(username: String): Boolean = username.none { it in " \n\r" }
}
