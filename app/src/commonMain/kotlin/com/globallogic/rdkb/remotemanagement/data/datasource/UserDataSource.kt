package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.domain.entity.User

interface UserDataSource {
    suspend fun addUser(email: String, name: String, password: String): User?

    suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): User?

    suspend fun isEmailUsed(email: String): Boolean

    suspend fun findUserByEmail(email: String): User?

    suspend fun findUserByCredentials(email: String, password: String): User?
}
