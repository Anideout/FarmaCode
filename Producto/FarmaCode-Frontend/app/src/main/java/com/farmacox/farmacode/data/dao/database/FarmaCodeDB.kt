package com.farmacox.farmacode.data.dao.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.farmacox.farmacode.data.dao.MedicationDao
import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.data.dao.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Medication::class, User::class], version = 2, exportSchema = false)
abstract class FarmaCodeDB : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
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
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback: Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                    database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.medicationDao())
                }
            }
        }

        suspend fun populateDatabase(medicationDao: MedicationDao) {
            val medications = listOf(
                Medication(
                    id = "1",
                    nombre= "Sertralina",
                    principioActivo = "Sertralina",
                    dosis = "50 mg",
                    presentacion = "28 comprimidos",
                    laboratorio = "Chile Laboratorios",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "AntiDepresivo",
                    certificacionISP = true,
                    descripcion = "Antidepresivo inhibidor selectivo de la recaptacion de serotonina (ISRS). Se utiliza para tratar la depresión, trastorno de pánico, trastorno de ansiedad social y trastorno obsesivo-compulsivo."
                ),Medication(
                    id = "2",
                    nombre = "Atorvastatina",
                    principioActivo = "Atorvastatina",
                    dosis = "20 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Chile Lab",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Hipolipemiante",
                    certificacionISP = true,
                    descripcion = "Estatina que reduce el colesterol LDL y los triglicéridos. Disminuye el riesgo de enfermedades cardiovasculares en pacientes con hipercolesterolemia."
                ),
                Medication(
                    id = "3",
                    nombre = "Losartán",
                    principioActivo = "Losartán",
                    dosis = "50 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Chile Labs",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antihipertensivo",
                    certificacionISP = true,
                    descripcion = "Antagonista de los receptores de angiotensina II. Se utiliza para tratar la hipertensión arterial y proteger los riñones en pacientes diabéticos."
                ),
                Medication(
                    id = "4",
                    nombre = "Omeprazol",
                    principioActivo = "Omeprazol",
                    dosis = "20 mg",
                    presentacion = "28 cápsulas",
                    laboratorio = "Chile Pharma",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antiulceroso",
                    certificacionISP = true,
                    descripcion = "Inhibidor de la bomba de protones. Reduce la producción de ácido gástrico. Indicado para úlceras gástricas, duodenales y reflujo gastroesofágico."
                ),
                Medication(
                    id = "5",
                    nombre = "Metformina",
                    principioActivo = "Metformina",
                    dosis = "850 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Chile Med",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antidiabético",
                    certificacionISP = true,
                    descripcion = "Antidiabético oral biguanida. Primera línea de tratamiento para diabetes tipo 2. Mejora la sensibilidad a la insulina y reduce la producción hepática de glucosa."
                ),
                Medication(
                    id = "6",
                    nombre = "Paracetamol",
                    principioActivo = "Paracetamol",
                    dosis = "500 mg",
                    presentacion = "20 comprimidos",
                    laboratorio = "Chile Labs",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Analgésico/Antipirético",
                    certificacionISP = true,
                    descripcion = "Analgésico y antipirético. Alivia el dolor leve a moderado y reduce la fiebre. No tiene propiedades antiinflamatorias significativas."
                ),
                Medication(
                    id = "7",
                    nombre = "Ibuprofeno",
                    principioActivo = "Ibuprofeno",
                    dosis = "400 mg",
                    presentacion = "20 comprimidos",
                    laboratorio = "Chile Pharma",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antiinflamatorio",
                    certificacionISP = true,
                    descripcion = "Antiinflamatorio no esteroideo (AINE). Tiene propiedades analgésicas, antiinflamatorias y antipiréticas. Indicado para dolor, inflamación y fiebre."
                ),
                Medication(
                    id = "8",
                    nombre = "Amoxicilina",
                    principioActivo = "Amoxicilina",
                    dosis = "500 mg",
                    presentacion = "21 cápsulas",
                    laboratorio = "Chile Lab",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antibiótico",
                    certificacionISP = true,
                    descripcion = "Antibiótico betalactámico del grupo de las aminopenicilinas. Amplio espectro contra infecciones bacterianas respiratorias, urinarias y de piel."
                ),
                Medication(
                    id = "9",
                    nombre = "Sertralina Ref",
                    principioActivo = "Sertralina",
                    dosis = "50 mg",
                    presentacion = "28 comprimidos",
                    laboratorio = "Pfizer",
                    paisOrigen = "Estados Unidos",
                    tipo = "Referencia",
                    categoriaTerapeutica = "Antidepresivo",
                    certificacionISP = true,
                    descripcion = "Medicamento de referencia para Sertralina. Antidepresivo ISRS de primera línea con amplia evidencia clínica."
                ),
                Medication(
                    id = "10",
                    nombre = "AtorvaBio",
                    principioActivo = "Atorvastatina",
                    dosis = "20 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Chile Pharma",
                    paisOrigen = "Chile",
                    tipo = "Bioequivalente",
                    categoriaTerapeutica = "Hipolipemiante",
                    certificacionISP = true,
                    descripcion = "Estatina bioequivalente. Demuestra equivalencia terapéutica con el medicamento de referencia. Misma eficacia y seguridad."
                ),
                Medication(
                    id = "11",
                    nombre = "Enalapril",
                    principioActivo = "Enalapril",
                    dosis = "10 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Chile Labs",
                    paisOrigen = "Chile",
                    tipo = "Genérico",
                    categoriaTerapeutica = "Antihipertensivo",
                    certificacionISP = true,
                    descripcion = "Inhibidor de la enzima convertidora de angiotensina (IECA). Trata la hipertensión y la insuficiencia cardíaca congestiva."
                ),
                Medication(
                    id = "12",
                    nombre = "Aspirina Protect",
                    principioActivo = "Ácido Acetilsalicílico",
                    dosis = "100 mg",
                    presentacion = "30 comprimidos",
                    laboratorio = "Bayer",
                    paisOrigen = "Alemania",
                    tipo = "Referencia",
                    categoriaTerapeutica = "Analgésico/Antipirético",
                    certificacionISP = true,
                    descripcion = "Antiinflamatorio y antiagregante plaquetario. Baja dosis para prevención cardiovascular. Alta dosis para dolor y fiebre."
                )
            )
            medicationDao.insertALL(medications)
        }
    }

}