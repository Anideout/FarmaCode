package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.User

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
}
