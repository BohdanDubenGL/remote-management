package com.globallogic.rdkb.remotemanagement.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User @OptIn(ExperimentalUuidApi::class) constructor(val uuid: Uuid, val username: String, val email: String) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        val empty: User = User(Uuid.NIL, "", "")
    }
}
