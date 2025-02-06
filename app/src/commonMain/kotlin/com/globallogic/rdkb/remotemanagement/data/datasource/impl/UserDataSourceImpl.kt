package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class UserDataSourceImpl(
    private val userDao: UserDao
): UserDataSource {

    override suspend fun addUser(email: String, name: String, password: String): Result<User> = runCatchingSafe {
        val userFromDb = userDao.findUserByEmail(email)
        if (userFromDb != null) error("User with email $email already exist")
        val newUser = UserDto(email = email, name = name, password = password)
        userDao.insertUser(newUser)
        UserMapper.toDomain(newUser)
    }

    override suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): Result<User> = runCatchingSafe {
        val user = userDao.findUserByEmail(email) ?: error("No user")
        val updatedUser = user.copy(email = newEmail, name = newName, password = newPassword)
        userDao.updateUser(updatedUser)
        UserMapper.toDomain(updatedUser)
    }

    override suspend fun isEmailUsed(email: String): Result<Boolean> = runCatchingSafe {
        userDao.isEmailUsed(email)
    }

    override suspend fun findUserByEmail(email: String): Result<User?> = runCatchingSafe {
        val user = userDao.findUserByEmail(email)
        user?.let(UserMapper::toDomain)
    }

    override suspend fun findUserByCredentials(email: String, password: String): Result<User?> = runCatchingSafe {
        val user = userDao.findUserByCredentials(email, password)
        user?.let(UserMapper::toDomain)
    }

}

private object UserMapper {
    fun toDomain(user: UserDto): User = User(username = user.name, email = user.email)
}
