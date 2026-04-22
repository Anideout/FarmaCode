package com.farmacox.farmacode.ui.theme.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen
import com.farmacox.farmacode.ui.theme.theme.SuccessGreen

@Composable
fun MedicationDetailDialog(
    medication: Medication,
    alternatives: List<Medication>,
    onDismiss: () -> Unit,
    onAlternativeClick: (Medication) -> Unit,
    fontSize: Float = 16f,
    language: String = "Español"
) {
    val isEnglish = language == "English"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = medication.nombre,
                            fontSize = (fontSize + 4).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = medication.dosis,
                            fontSize = fontSize.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeBadge(tipo = medication.tipo, fontSize = fontSize)
                if (medication.certificacionISP) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = if (isEnglish) "ISP Certified" else "Certificado ISP",
                                fontSize = (fontSize - 4).sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                icon = Icons.Default.Science,
                label = if (isEnglish) "Active Ingredient" else "Principio Activo",
                value = medication.principioActivo,
                fontSize = fontSize
            )
            DetailRow(
                icon = Icons.Default.LocalPharmacy,
                label = if (isEnglish) "Presentation" else "Presentación",
                value = medication.presentacion,
                fontSize = fontSize
            )
            DetailRow(
                icon = Icons.Default.Factory,
                label = if (isEnglish) "Laboratory" else "Laboratorio",
                value = medication.laboratorio,
                fontSize = fontSize
            )
            DetailRow(
                icon = Icons.Default.Flag,
                label = if (isEnglish) "Country of Origin" else "País de Origen",
                value = medication.paisOrigen,
                fontSize = fontSize
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEnglish) "Description" else "Descripción",
                fontSize = fontSize.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = medication.descripcion,
                fontSize = (fontSize - 2).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (alternatives.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isEnglish) "Alternatives (${alternatives.size})" else "Alternativas (${alternatives.size})",
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                alternatives.forEach { alternative ->
                    AlternativeItem(
                        medication = alternative,
                        onClick = { onAlternativeClick(alternative) },
                        fontSize = fontSize
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    fontSize: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(
                text = label,
                fontSize = (fontSize - 6).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = (fontSize - 2).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AlternativeItem(
    medication: Medication,
    onClick: () -> Unit,
    fontSize: Float
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.nombre,
                    fontSize = (fontSize - 2).sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${medication.tipo} - ${medication.laboratorio}",
                    fontSize = (fontSize - 6).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (medication.certificacionISP) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Certificado",
                    tint = SuccessGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
