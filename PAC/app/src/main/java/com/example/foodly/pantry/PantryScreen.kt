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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    viewModel: PantryViewModel = viewModel()
) {
    val userPantryItems by viewModel.userPantryItems.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    // For editing, null if adding new
    var editingItem by remember { mutableStateOf<UserPantryItem?>(null) }
    val context = LocalContext.current

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize().background(gradient),
        containerColor = Color.Transparent,
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
                Button(
                    onClick = {
                        Toast.makeText(context, "Recipe recommendation coming soon!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Keep padding for the button itself
                    // Uses default M3 Button styling (primary container)
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
                    items(userPantryItems, key = { it.id }) { item ->
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
                        viewModel.addPantryItem(pantryItem, quantity, unit)
                    } else { // Editing existing
                        viewModel.updatePantryItem(pantryItem.id, quantity, unit)
                    }
                    showDialog = false
                }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Keep low elevation
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use surface color
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
    onConfirm: (selectedItem: PantryIngredientItem, quantity: Double, unit: String) -> Unit
) {
    var selectedIngredient by remember {
        mutableStateOf(editingItem?.pantryIngredient ?: viewModel.predefinedIngredients.first())
    }
    var quantityStr by rememberSaveable(editingItem) {
        mutableStateOf(editingItem?.quantity?.toString() ?: "")
    }
    var unit by rememberSaveable(editingItem) {
        mutableStateOf(editingItem?.unit ?: selectedIngredient.defaultUnit)
    }
    var expandedDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Update unit when selectedIngredient changes, but only if not editing or unit is default
    LaunchedEffect(selectedIngredient, editingItem) {
        if (editingItem == null || unit == editingItem.pantryIngredient.defaultUnit) {
            unit = selectedIngredient.defaultUnit
        }
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingItem == null) "Add Ingredient" else "Edit Ingredient") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Ingredient Dropdown
                Box {
                    OutlinedTextField(
                        value = selectedIngredient.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ingredient") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Select ingredient", Modifier.clickable { expandedDropdown = true }) },
                        modifier = Modifier.fillMaxWidth(),
                        // Rely on M3 default colors for OutlinedTextField
                    )
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f), // Adjust width as needed
                        // DropdownMenu background defaults to M3 surface
                    ) {
                        viewModel.predefinedIngredients.forEach { ingredient ->
                            DropdownMenuItem(
                                text = { Text(ingredient.name, color = MaterialTheme.colorScheme.onSurface) }, // Ensure text color
                                onClick = {
                                    selectedIngredient = ingredient
                                    unit = ingredient.defaultUnit // Reset unit to default of new ingredient
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // Quantity TextField - Rely on M3 defaults
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Unit TextField - Rely on M3 defaults
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantity = quantityStr.toDoubleOrNull()
                    if (quantity == null || quantity <= 0) {
                        Toast.makeText(context, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (unit.isBlank()) {
                        Toast.makeText(context, "Please enter a unit.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onConfirm(selectedIngredient, quantity, unit)
                }
                // Default M3 Button styling
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
                // Default M3 TextButton styling (text color will be primary)
            ) {
                Text("Cancel")
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
