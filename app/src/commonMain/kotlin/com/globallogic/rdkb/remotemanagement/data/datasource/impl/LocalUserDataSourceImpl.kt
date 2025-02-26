package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalUserDataSource
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.data.error.IoUserError
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class LocalUserDataSourceImpl(
    private val userDao: UserDao
): LocalUserDataSource {

    override suspend fun addUser(email: String, name: String, password: String): Resource<User, IoUserError.AddUser> {
        val userFromDb = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }
        if (userFromDb != null) return Failure(IoUserError.UserAlreadyExist)

        val newUser = UserDto(email = email, name = name, password = password)
        runCatchingSafe { userDao.upsertUser(newUser) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }
        return Success(UserMapper.toDomain(newUser))
    }

    override suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): Resource<User, IoUserError.UpdateUser> {
        val user = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }
            ?: return Failure(IoUserError.UserNotFound)

        val updatedUser = user.copy(email = newEmail, name = newName, password = newPassword)
        runCatchingSafe { userDao.upsertUser(updatedUser) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }

        return Success(UserMapper.toDomain(updatedUser))
    }

    override suspend fun findUserByEmail(email: String): Resource<User, IoUserError.FindUserById> {
        val user = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }
            ?: return Failure(IoUserError.UserNotFound)

        return Success(UserMapper.toDomain(user))
    }

    override suspend fun findUserByCredentials(email: String, password: String): Resource<User, IoUserError.FindUserByCredentials> {
        val user = runCatchingSafe { userDao.findUserByCredentials(email, password) }
            .getOrElse { error -> return Failure(IoUserError.DatabaseError(error)) }
            ?: return Failure(IoUserError.UserNotFound)

        return Success(UserMapper.toDomain(user))
    }
}

private object UserMapper {
    fun toDomain(user: UserDto): User = User(username = user.name, email = user.email)
}
