package com.globallogic.rdkb.remotemanagement.domain.entity

data class RegistrationData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
