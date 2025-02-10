package com.globallogic.rdkb.remotemanagement.domain.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface UserError : ResourceError {
    sealed interface ChangeAccountSettingsError : UserError
    sealed interface LoginError : UserError
    sealed interface RegistrationError : UserError
    sealed interface LogoutError : UserError

    data object NoLoggedInUser : UserError
    data object ConfirmPasswordDoesntMatch : RegistrationError, ChangeAccountSettingsError
    data object UserNotFound : ChangeAccountSettingsError
    data object WrongCredentials : LoginError, ChangeAccountSettingsError
    data object UserAlreadyExist : RegistrationError
    data class WrongUsernameLength(val min: Int, val max: Int) : RegistrationError, ChangeAccountSettingsError
    data object WrongUsernameFormat : RegistrationError, ChangeAccountSettingsError
    data object WrongEmailFormat : ChangeAccountSettingsError
    data class WrongPasswordLength(val min: Int, val max: Int) : LoginError, RegistrationError, ChangeAccountSettingsError
    data object WrongPasswordFormat : LoginError, RegistrationError, ChangeAccountSettingsError
}
