package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDto)

    @Upsert
    suspend fun updateUser(user: UserDto)

    @Query("SELECT * FROM user where email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): UserDto?

    @Query("SELECT * FROM user where email = :email AND password = :password LIMIT 1")
    suspend fun findUserByCredentials(email: String, password: String): UserDto?

    @Query("SELECT 1 FROM user where email = :email LIMIT 1")
    suspend fun isEmailUsed(email: String): Boolean
}
