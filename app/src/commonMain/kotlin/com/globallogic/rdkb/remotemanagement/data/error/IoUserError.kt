package com.globallogic.rdkb.remotemanagement.data.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface IoUserError: ResourceError {
    sealed interface AddUserError : IoUserError
    sealed interface UpdateUserError : IoUserError
    sealed interface FindUserByIdError : IoUserError
    sealed interface FindUserByCredentialsError : IoUserError

    data class DatabaseError(val throwable: Throwable) : AddUserError, UpdateUserError, FindUserByIdError, FindUserByCredentialsError
    data object UserAlreadyExist: AddUserError
    data object UserNotFound: UpdateUserError, FindUserByIdError, FindUserByCredentialsError
}
