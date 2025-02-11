package com.globallogic.rdkb.remotemanagement.data.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface IoUserError: ResourceError {
    sealed interface AddUser : IoUserError
    sealed interface UpdateUser : IoUserError
    sealed interface FindUserById : IoUserError
    sealed interface FindUserByCredentials : IoUserError

    data object UserAlreadyExist : AddUser
    data object UserNotFound : UpdateUser, FindUserById, FindUserByCredentials

    data class DatabaseError(val throwable: Throwable) : AddUser, UpdateUser,
        FindUserById, FindUserByCredentials
}
