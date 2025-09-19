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
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Dispensa",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                )
            )
        },
        floatingActionButton = {
            // Empty - we moved the button to bottomBar
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // FAB a destra
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                editingItem = null
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Ingredient")
                            Spacer(Modifier.width(8.dp))
                            Text("Aggiungi")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Bottone principale
                    Button(
                        onClick = {
                            onRecipeRecommendationClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Consigliami una ricetta",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (userPantryItems.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "La tua dispensa è vuota",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Aggiungi alcuni ingredienti per iniziare",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userPantryItems, key = { it.apiId ?: it.id }) { item ->
                        PantryListItem(
                            item = item,
                            onRemove = { viewModel.removePantryItem(context, item.id) },
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.pantryIngredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            IconButton(
                onClick = onRemove,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error
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
        title = { 
            Text(
                if (editingItem == null) "Aggiungi Ingrediente" else "Modifica Ingrediente",
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
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
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Aggiungendo ingrediente...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button (
                onClick = {
                    val quantity = quantityStr.toDoubleOrNull()
                    if (quantity == null || quantity <= 0) {
                        Toast.makeText(context, "Inserisci una quantità valida.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val unitString = when (selectedUnitType) {
                        UnitType.GRAMS -> "g"
                        UnitType.UNITS -> "unità"
                    }

                    onConfirm(selectedIngredient, quantity, unitString)
                },
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Conferma")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Annulla")
            }
        },
        shape = MaterialTheme.shapes.extraLarge
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
