package com.farmacox.farmacode.data.dao.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.User

@Database(entities = [User::class], version = 3, exportSchema = false)
abstract class FarmaCodeDB : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: FarmaCodeDB? = null

        fun getDatabase(context: Context): FarmaCodeDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmaCodeDB::class.java,
                    "farmacode_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
