package com.farmacox.farmacode

import android.app.Application
import com.farmacox.farmacode.data.dao.database.FarmaCodeDB
import com.farmacox.farmacode.data.network.RetrofitClient
import com.farmacox.farmacode.repository.MedicationRepository
import com.farmacox.farmacode.repository.UserRepository

class FarmaCodeApp : Application() {
    val database by lazy { FarmaCodeDB.getDatabase(this) }
    val repository by lazy { MedicationRepository(RetrofitClient.apiService) }
    val userRepository by lazy { UserRepository(database.userDao()) }
}
