package com.farmacox.farmacode.ui.theme.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen
import com.farmacox.farmacode.ui.theme.theme.WarningAmber

@Composable
fun HelpScreen(
    fontSize: Float,
    language: String
) {
    val isEnglish = language == "English"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryGreen,
                            PrimaryGreen.copy(alpha = 0.8f)
                        )
                    )
                )
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = if (isEnglish) "Help Center" else "Centro de Ayuda",
                    fontSize = (fontSize + 8).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = if (isEnglish) "Learn how to use FarmaCode" else "Aprende a usar FarmaCode",
                    fontSize = fontSize.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEnglish) "How to use the app" else "Cómo usar la aplicación",
                fontSize = (fontSize + 2).sp,
                fontWeight = FontWeight.SemiBold
            )

            HelpStepCard(
                step = 1,
                icon = Icons.Default.Search,
                title = if (isEnglish) "Search medications" else "Buscar medicamentos",
                description = if (isEnglish) "Use the search bar to find medications by name, active ingredient or lab." else "Usa la barra de búsqueda para encontrar medicamentos por nombre, principio activo o laboratorio.",
                fontSize = fontSize
            )

            HelpStepCard(
                step = 2,
                icon = Icons.Default.QrCodeScanner,
                title = if (isEnglish) "Scan code" else "Escanear código",
                description = if (isEnglish) "Use the scanner to read the medication's barcode and get instant info." else "Usa el escáner para leer el código de barras del medicamento y obtener información instantánea.",
                fontSize = fontSize
            )

            HelpStepCard(
                step = 3,
                icon = Icons.Default.LocalPharmacy,
                title = if (isEnglish) "View alternatives" else "Ver alternativas",
                description = if (isEnglish) "Find generic and bioequivalent alternatives with the same active ingredient." else "Encuentra alternativas genéricas y bioequivalentes con el mismo principio activo.",
                fontSize = fontSize
            )

            HelpStepCard(
                step = 4,
                icon = Icons.Default.CheckCircle,
                title = if (isEnglish) "Verify certification" else "Verificar certificación",
                description = if (isEnglish) "All medications show their ISP certification to ensure quality and safety." else "Todos los medicamentos muestran su certificación ISP para garantizar calidad y seguridad.",
                fontSize = fontSize
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEnglish) "Glossary of terms" else "Glosario de términos",
                fontSize = (fontSize + 2).sp,
                fontWeight = FontWeight.SemiBold
            )

            GlossaryCard(
                term = if (isEnglish) "Generic" else "Genérico",
                definition = if (isEnglish) "Medication that contains the same active ingredient as the reference one, but may vary in excipients." else "Medicamento que contiene el mismo principio activo que el de referencia, pero puede variar en excipientes y presentación.",
                fontSize = fontSize
            )

            GlossaryCard(
                term = if (isEnglish) "Bioequivalent" else "Bioequivalente",
                definition = if (isEnglish) "Medication that proves to have the same bioavailability as the reference drug, ensuring the same effect." else "Medicamento que demuestra tener la misma biodisponibilidad que el medicamento de referencia, garantizando el mismo efecto terapéutico.",
                fontSize = fontSize
            )

            GlossaryCard(
                term = if (isEnglish) "Reference" else "Referencia",
                definition = if (isEnglish) "Original medication registered with enough scientific documentation about its efficacy and safety." else "Medicamento original que ha sido registrado con suficiente documentación científica sobre su eficacia y seguridad.",
                fontSize = fontSize
            )

            GlossaryCard(
                term = if (isEnglish) "Active Ingredient" else "Principio Activo",
                definition = if (isEnglish) "Substance responsible for the therapeutic action of the medication." else "Sustancia responsable de la acción terapéutica del medicamento.",
                fontSize = fontSize
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WarningAmber.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Safety notice" else "Aviso de seguridad",
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isEnglish) "This app is for information only. Always consult a health professional before taking any medication." else "Esta aplicación es solo informativa. Consulta siempre con un profesional de salud antes de tomar cualquier medicamento. No sustituye el consejo médico profesional.",
                            fontSize = (fontSize - 2).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HelpStepCard(
    step: Int,
    icon: ImageVector,
    title: String,
    description: String,
    fontSize: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.toString(),
                    fontSize = (fontSize + 2).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = (fontSize - 2).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GlossaryCard(
    term: String,
    definition: String,
    fontSize: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = term,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = definition,
                fontSize = (fontSize - 2).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
