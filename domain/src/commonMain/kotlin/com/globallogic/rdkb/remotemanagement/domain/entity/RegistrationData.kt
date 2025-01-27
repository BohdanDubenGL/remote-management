package com.globallogic.rdkb.remotemanagement.domain.entity

data class RegistrationData(
    val username: String,
    val email: String,
    val passwordHash: String,
    val repeatPasswordHash: String
)
