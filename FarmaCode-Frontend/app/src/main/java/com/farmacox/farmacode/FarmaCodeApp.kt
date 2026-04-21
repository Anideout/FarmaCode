package com.farmacox.farmacode

import android.app.Application
import com.farmacox.farmacode.data.dao.database.FarmaCodeDB
import com.farmacox.farmacode.repository.MedicationRepository

class FarmaCodeApp : Application() {
    val database by lazy { FarmaCodeDB.getDatabase(this) }
    val repository by lazy { MedicationRepository(database.medicationDao())}
}