package com.globallogic.rdkb.remotemanagement.domain.entity

data class LoginData(
    val username: String,
    val email: String,
    val passwordHash: String
)
