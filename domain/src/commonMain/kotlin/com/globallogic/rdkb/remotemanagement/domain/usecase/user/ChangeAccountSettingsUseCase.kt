package com.globallogic.rdkb.remotemanagement.domain.usecase.user

import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.repository.UserRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class ChangeAccountSettingsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(settingsData: ChangeAccountSettingsData): Result<Boolean> = changeAccountSettings(settingsData)

    suspend fun changeAccountSettings(settingsData: ChangeAccountSettingsData): Result<Boolean> = runCatchingSafe {
        if (settingsData.password != settingsData.confirmPassword) return@runCatchingSafe false
        userRepository.changeAccountSettings(settingsData).getOrThrow()
        return@runCatchingSafe true
    }
}
