package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: UserDto)

    @Query("SELECT * FROM user where email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): UserDto?

    @Query("SELECT * FROM user where email = :email AND password = :password LIMIT 1")
    suspend fun findUserByCredentials(email: String, password: String): UserDto?
}
