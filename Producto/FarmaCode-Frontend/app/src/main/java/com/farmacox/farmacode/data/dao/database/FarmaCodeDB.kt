package com.farmacox.farmacode.data.dao.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.farmacox.farmacode.data.dao.ScanHistoryDao
import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import com.farmacox.farmacode.data.dao.entity.User

@Database(entities = [User::class, ScanHistory::class], version = 5, exportSchema = false)
abstract class FarmaCodeDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scanHistoryDao(): ScanHistoryDao


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
