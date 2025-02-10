package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.data.error.IoUserError
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface UserDataSource {
    suspend fun addUser(email: String, name: String, password: String): Resource<User, IoUserError.AddUserError>

    suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): Resource<User, IoUserError.UpdateUserError>

    suspend fun findUserByEmail(email: String): Resource<User, IoUserError.FindUserByIdError>

    suspend fun findUserByCredentials(email: String, password: String): Resource<User, IoUserError.FindUserByCredentialsError>
}
