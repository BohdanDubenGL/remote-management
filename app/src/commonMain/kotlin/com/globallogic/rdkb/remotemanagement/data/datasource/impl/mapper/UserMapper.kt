package com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper

import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.domain.entity.User

object UserMapper {
    fun toUser(user: UserDto): User = User(username = user.name, email = user.email)
}
