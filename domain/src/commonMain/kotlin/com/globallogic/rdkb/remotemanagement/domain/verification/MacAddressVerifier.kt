package com.globallogic.rdkb.remotemanagement.domain.verification

class MacAddressVerifier(
    private val macAddressRegex: Regex = defaultMacAddressRegex
) {
    fun verifyMacAddress(macAddress: String): List<Error> = buildList {
        if (macAddress.isBlank()) {
            add(Error.EmptyMacAddress)
            return@buildList
        }
        if (!macAddressRegex.matches(macAddress)) add(Error.InvalidFormat)
    }

    sealed interface Error {
        data object EmptyMacAddress : Error
        data object InvalidFormat : Error
    }

    companion object {
        private val defaultMacAddressRegex: Regex = "^([0-9A-Fa-f]{2}([ :])?){5}[0-9A-Fa-f]{2}\$".toRegex()
    }
}
