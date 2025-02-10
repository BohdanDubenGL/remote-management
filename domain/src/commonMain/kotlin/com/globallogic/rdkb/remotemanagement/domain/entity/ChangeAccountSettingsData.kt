package com.globallogic.rdkb.remotemanagement.domain.entity

data class ChangeAccountSettingsData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)
