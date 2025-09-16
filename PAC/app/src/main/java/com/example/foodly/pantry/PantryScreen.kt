package com.example.foodly.pantry

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.api.UnitType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    viewModel: PantryViewModel = viewModel(),
    onRecipeRecommendationClick: () -> Unit = {}
) {
    val userPantryItems by viewModel.userPantryItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    // For editing, null if adding new
    var editingItem by remember { mutableStateOf<UserPantryItem?>(null) }
    val context = LocalContext.current

    // Carica la dispensa quando il composable viene creato
    LaunchedEffect(Unit) {
        viewModel.loadPantry(context)
    }

    // Mostra messaggio di errore se presente
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background, // Set screen background color
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null // Ensure we are in "add mode"
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer, // M3 standard FAB color
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Ingredient")
            }
        },
        bottomBar = {
            // Consider using a BottomAppBar if more actions are planned, or just a styled Button
            Surface( // Add a surface for elevation and theming if desired for bottom bar area
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp, // Example elevation for bottom bar area
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) // Use surfaceColorAtElevation
            ) {
                FilledTonalButton(
                    onClick = {
                        onRecipeRecommendationClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Keep padding for the button itself
                    // Uses tonal styling for a softer look
                ) {
                    Text("Consigliami una ricetta")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) { // Ensure column fills size
            if (userPantryItems.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Your pantry is empty. Add some ingredients!",
                        style = MaterialTheme.typography.titleMedium, // Use theme typography
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme color
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(all = 16.dp), // Consistent padding
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userPantryItems, key = { it.apiId ?: it.id }) { item ->
                        PantryListItem(
                            item = item,
                            onRemove = { viewModel.removePantryItem(item.id) },
                            onEdit = {
                                editingItem = item
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddEditPantryItemDialog(
                viewModel = viewModel,
                editingItem = editingItem,
                onDismiss = { showDialog = false },
                onConfirm = { pantryItem, quantity, unit ->
                    if (editingItem == null) { // Adding new
                        viewModel.addPantryItem(context, pantryItem, quantity, unit)
                    } else { // Editing existing
                        viewModel.updatePantryItem(pantryItem.id, quantity, unit)
                    }
                    showDialog = false
                },
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun PantryListItem(
    item: UserPantryItem,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp) // Consistent padding
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.pantryIngredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface // Theme color
                )
                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme color
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error // Use error color for delete icon
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPantryItemDialog(
    viewModel: PantryViewModel,
    editingItem: UserPantryItem?, // if null, it's an "Add" dialog
    onDismiss: () -> Unit,
    onConfirm: (selectedItem: PantryIngredientItem, quantity: Double, unit: String) -> Unit,
    isLoading: Boolean = false
) {
    var selectedIngredient by remember {
        mutableStateOf(editingItem?.pantryIngredient ?: viewModel.predefinedIngredients.first())
    }
    var quantityStr by rememberSaveable(editingItem) {
        mutableStateOf(editingItem?.quantity?.toString() ?: "")
    }
    var selectedUnitType by remember {
        mutableStateOf(
            if (editingItem?.unit?.lowercase() in listOf("g", "kg", "grams", "kilograms")) {
                UnitType.GRAMS
            } else {
                UnitType.UNITS
            }
        )
    }
    var expandedIngredientDropdown by remember { mutableStateOf(false) }
    var expandedUnitDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val unitOptions = listOf(
        UnitType.UNITS to "Unità",
        UnitType.GRAMS to "Grammi"
    )

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(if (editingItem == null) "Aggiungi Ingrediente" else "Modifica Ingrediente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Ingredient Dropdown
                Box {
                    OutlinedTextField(
                        value = selectedIngredient.name,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        label = { Text("Ingrediente") },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                "Seleziona ingrediente",
                                Modifier.clickable { if (!isLoading) expandedIngredientDropdown = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenu(
                        expanded = expandedIngredientDropdown,
                        onDismissRequest = { expandedIngredientDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        viewModel.predefinedIngredients.forEach { ingredient ->
                            DropdownMenuItem(
                                text = { Text(ingredient.name, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    selectedIngredient = ingredient
                                    expandedIngredientDropdown = false
                                }
                            )
                        }
                    }
                }

                // Quantity TextField
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it.filter { char -> char.isDigit() || char == '.' } },
                    enabled = !isLoading,
                    label = { Text("Quantità") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Unit Type Dropdown
                Box {
                    OutlinedTextField(
                        value = unitOptions.find { it.first == selectedUnitType }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        label = { Text("Tipo di Unità") },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                "Seleziona unità",
                                Modifier.clickable { if (!isLoading) expandedUnitDropdown = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenu(
                        expanded = expandedUnitDropdown,
                        onDismissRequest = { expandedUnitDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        unitOptions.forEach { (unitType, displayName) ->
                            DropdownMenuItem(
                                text = { Text(displayName, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    selectedUnitType = unitType
                                    expandedUnitDropdown = false
                                }
                            )
                        }
                    }
                }

                // Mostra indicatore di caricamento se necessario
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aggiungendo ingrediente...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            ElevatedButton(
                onClick = {
                    val quantity = quantityStr.toDoubleOrNull()
                    if (quantity == null || quantity <= 0) {
                        Toast.makeText(context, "Inserisci una quantità valida.", Toast.LENGTH_SHORT).show()
                        return@ElevatedButton
                    }

                    val unitString = when (selectedUnitType) {
                        UnitType.GRAMS -> "g"
                        UnitType.UNITS -> "unità"
                    }

                    onConfirm(selectedIngredient, quantity, unitString)
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Conferma")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Annulla")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantryScreenPreview() {
    com.example.foodly.ui.theme.FoodlyTheme {
        PantryScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PantryListItemPreview() {
    com.example.foodly.ui.theme.FoodlyTheme {
        PantryListItem(
            item = UserPantryItem(PredefinedIngredients.items.first(), 100.0, "g"),
            onRemove = {},
            onEdit = {}
        )
    }
}
