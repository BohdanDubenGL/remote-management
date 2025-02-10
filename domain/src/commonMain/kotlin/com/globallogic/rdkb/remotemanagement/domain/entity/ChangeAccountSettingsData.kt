package com.globallogic.rdkb.remotemanagement.domain.entity

data class ChangeAccountSettingsData(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,
    val currentPassword: String,
)
