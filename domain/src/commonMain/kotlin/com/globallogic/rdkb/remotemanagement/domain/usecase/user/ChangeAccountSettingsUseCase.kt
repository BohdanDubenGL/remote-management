package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository

class ChangeAccountSettingsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(settingsData: ChangeAccountSettingsData): Boolean = changeAccountSettings(settingsData)

    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Boolean {
        if (settingsData.password != settingsData.confirmPassword) return false
        userRepository.changeAccountSettings(settingsData)
        return true
    }
}
