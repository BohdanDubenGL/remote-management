package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.UserDataSource
import com.globallogic.rdkb.remotemanagement.data.db.UserDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.data.error.IoUserError
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class UserDataSourceImpl(
    private val userDao: UserDao
): UserDataSource {

    override suspend fun addUser(email: String, name: String, password: String): Resource<User, IoUserError.AddUser> = buildResource {
        val userFromDb = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }
        if (userFromDb != null) return failure(IoUserError.UserAlreadyExist)

        val newUser = UserDto(email = email, name = name, password = password)
        runCatchingSafe { userDao.insertUser(newUser) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }
        return success(UserMapper.toDomain(newUser))
    }

    override suspend fun updateUser(email: String, newEmail: String, newName: String, newPassword: String): Resource<User, IoUserError.UpdateUser> = buildResource {
        val user = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }
            ?: return failure(IoUserError.UserNotFound)

        val updatedUser = user.copy(email = newEmail, name = newName, password = newPassword)
        runCatchingSafe { userDao.updateUser(updatedUser) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }

        return success(UserMapper.toDomain(updatedUser))
    }

    override suspend fun findUserByEmail(email: String): Resource<User, IoUserError.FindUserById> = buildResource {
        val user = runCatchingSafe { userDao.findUserByEmail(email) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }
            ?: return failure(IoUserError.UserNotFound)

        return success(UserMapper.toDomain(user))
    }

    override suspend fun findUserByCredentials(email: String, password: String): Resource<User, IoUserError.FindUserByCredentials> = buildResource {
        val user = runCatchingSafe { userDao.findUserByCredentials(email, password) }
            .getOrElse { error -> return failure(IoUserError.DatabaseError(error)) }
            ?: return failure(IoUserError.UserNotFound)

        return success(UserMapper.toDomain(user))
    }
}

private object UserMapper {
    fun toDomain(user: UserDto): User = User(username = user.name, email = user.email)
}
