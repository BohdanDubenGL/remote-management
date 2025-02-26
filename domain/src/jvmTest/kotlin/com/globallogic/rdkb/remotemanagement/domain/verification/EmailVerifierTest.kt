package com.globallogic.rdkb.remotemanagement.domain.verification

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty

class EmailVerifierTest {
    private val emailVerifier = EmailVerifier()

    @Test
    fun `verifyEmailFormat should return true for valid email formats`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name+tag+sorting@example.com",
            "user_name@example.co.uk",
            "user-name@sub.example.io",
            "user12345@example.com",
            "example@localhost.localdomain",
            "name_surname@company.domain"
        )
        validEmails.forEach { email ->
            expectThat(emailVerifier.verifyEmail(email)).isEmpty()
        }
    }

    @Test
    fun `verifyEmailFormat should return false for empty email`() {
        val email = ""
        expectThat(emailVerifier.verifyEmail(email)).containsExactly(EmailVerifier.Error.EmptyEmail)
    }

    @Test
    fun `verifyEmailFormat should return false for invalid email formats`() {
        val invalidEmails = listOf(
            "plainaddress",
            "@missingusername.com",
            "username@.missingdomain",
            "user@domain..com",
            "user@domain_com",
            "user@@domain.com",
            "user@domain@domain.com",
            "user@domain,com",
            "!invalid%@example.com",
            "space in@domain.com"
        )

        invalidEmails.forEach { email ->
            expectThat(emailVerifier.verifyEmail(email)).containsExactly(EmailVerifier.Error.InvalidFormat)
        }
    }
}
