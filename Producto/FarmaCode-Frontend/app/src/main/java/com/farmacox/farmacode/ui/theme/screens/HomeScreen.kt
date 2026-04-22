package com.farmacox.farmacode.ui.theme.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farmacox.farmacode.FarmaCodeApp
import com.farmacox.farmacode.R
import com.farmacox.farmacode.ui.theme.components.MedicationCard
import com.farmacox.farmacode.ui.theme.components.MedicationDetailDialog
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen
import com.farmacox.farmacode.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            item {
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

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.categories.forEach { category ->
                        val translatedCategory = when(category) {
                            "Todos" -> if(isEnglish) "All" else "Todos"
                            "Analgésicos" -> if(isEnglish) "Painkillers" else "Analgésicos"
                            "Antibióticos" -> if(isEnglish) "Antibiotics" else "Antibióticos"
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
