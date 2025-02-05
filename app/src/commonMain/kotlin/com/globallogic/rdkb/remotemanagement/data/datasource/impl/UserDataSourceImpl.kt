package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.domain.entity.User

class UserDataSourceImpl(
    private val userDao: UserDao
): UserDataSource {

    override suspend fun addUser(email: String, name: String, password: String): User {
        val userFromDb = userDao.findUserByEmail(email)
        if (userFromDb != null) error("User with email $email already exist")
        val newUser = UserDto(email = email, name = name, password = password)
        userDao.insertUser(newUser)
        return UserMapper.toDomain(newUser)
    }

    override suspend fun updateUser(
        email: String,
        newEmail: String,
        newName: String,
        newPassword: String
    ): User? {
        val user = userDao.findUserByEmail(email) ?: return null
        val updatedUser = user.copy(email = newEmail, name = newName, password = newPassword)
        userDao.updateUser(updatedUser)
        return UserMapper.toDomain(updatedUser)
    }

    override suspend fun isEmailUsed(email: String): Boolean {
        return userDao.isEmailUsed(email)
    }

    override suspend fun findUserByEmail(email: String): User {
        val user = userDao.findUserByEmail(email)
        return UserMapper.toDomain(user)
    }

    override suspend fun findUserByCredentials(email: String, password: String): User {
        val user = userDao.findUserByCredentials(email, password)
        return UserMapper.toDomain(user)
    }

}

private object UserMapper {
    fun toDomain(user: UserDto?): User = when(user) {
        null -> User.empty
        else -> User(username = user.name, email = user.email)
    }
}
