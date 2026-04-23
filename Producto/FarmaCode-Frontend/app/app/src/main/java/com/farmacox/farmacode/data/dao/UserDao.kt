package com.farmacox.farmacode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.farmacox.farmacode.data.dao.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert
    suspend fun insertUser(user: User)
}
