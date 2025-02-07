package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.domain.entity.User

interface UserDataSource {
    suspend fun addUser(email: String, name: String, password: String): Result<User>

    suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): Result<User>

    suspend fun findUserByEmail(email: String): Result<User?>

    suspend fun findUserByCredentials(email: String, password: String): Result<User?>
}
