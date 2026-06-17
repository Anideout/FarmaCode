package com.farmacox.farmacode.ui.theme.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farmacox.farmacode.FarmaCodeApp
import com.farmacox.farmacode.R
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import com.farmacox.farmacode.ui.theme.components.MedicationCard
import com.farmacox.farmacode.ui.theme.components.MedicationDetailDialog
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen
import com.farmacox.farmacode.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    fontSize: Float,
    language: String
) {
    val context = LocalContext.current
    val app = context.applicationContext as FarmaCodeApp
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(app.repository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDialog by remember { mutableStateOf(false) }

    val isEnglish = language == "English"

    if (uiState.selectedMedication != null) {
        showDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header con buscador
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryGreen, PrimaryGreen.copy(alpha = 0.8f))
                            )
                        )
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "Logo FarmaCode",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "FarmaCode",
                                        fontSize = (fontSize + 8).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = if (isEnglish) "ISP Certified Medications" else "Medicamentos certificados ISP",
                                        fontSize = fontSize.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            IconButton(onClick = onToggleTheme) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Cambiar tema",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    if (isEnglish) "Search medication..." else "Buscar medicamento...",
                                    fontSize = fontSize.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Filtros de categoría — sticky para que queden fijos al hacer scroll
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.categories.forEach { category ->
                        val translatedCategory = when (category) {
                            "Todos" -> if (isEnglish) "All" else "Todos"
                            "Analgésicos" -> if (isEnglish) "Painkillers" else "Analgésicos"
                            "Antibióticos" -> if (isEnglish) "Antibiotics" else "Antibióticos"
                            else -> category
                        }
                        val isSelected = category == uiState.selectedCategory ||
                                (category == "Todos" && uiState.selectedCategory == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onCategorySelected(if (category == "Todos") null else category)
                            },
                            label = { Text(translatedCategory, fontSize = (fontSize - 2).sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Historial de escaneos
            if (uiState.scanHistory.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Recently Scanned" else "Últimos escaneados",
                            fontSize = (fontSize + 1).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                items(uiState.scanHistory) { scan ->
                    ScanHistoryRow(
                        scan = scan,
                        fontSize = fontSize,
                        onClick = { viewModel.onScanHistorySelected(scan) },
                        onDelete = { viewModel.onDeleteScanHistory(scan) }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            // Lista de medicamentos
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
            } else {
                items(uiState.medications) { medication ->
                    MedicationCard(
                        medication = medication,
                        onClick = { viewModel.onMedicationSelected(medication) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = fontSize
                    )
                }
            }
        }

        if (showDialog && uiState.selectedMedication != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDialog = false
                    viewModel.onDismissDialog()
                },
                sheetState = sheetState
            ) {
                MedicationDetailDialog(
                    medication = uiState.selectedMedication!!,
                    alternatives = uiState.alternatives,
                    onDismiss = {
                        showDialog = false
                        viewModel.onDismissDialog()
                    },
                    onAlternativeClick = { alternative ->
                        viewModel.onMedicationSelected(alternative)
                    },
                    fontSize = fontSize,
                    language = language
                )
            }
        }
    }
}

@Composable
private fun ScanHistoryRow(
    scan: ScanHistory,
    fontSize: Float,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val tipoNorm = scan.tipo.trim().lowercase()
    val showTipoBadge = tipoNorm.isNotBlank() && tipoNorm != "escaneado" && tipoNorm != "n/d"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.nombre,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${scan.principioActivo} · ${scan.dosis}",
                    fontSize = (fontSize - 2).sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(6.dp))

            if (showTipoBadge) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (tipoNorm) {
                                "genérico", "generico" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                "bioequivalente" -> Color(0xFF2196F3).copy(alpha = 0.15f)
                                "referencia" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = scan.tipo,
                        fontSize = (fontSize - 3).sp,
                        color = when (tipoNorm) {
                            "genérico", "generico" -> Color(0xFF388E3C)
                            "bioequivalente" -> Color(0xFF1565C0)
                            "referencia" -> Color(0xFFE65100)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.width(2.dp))
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
