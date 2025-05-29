package com.example.foodly.recipes


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RecipesScreen() {
    // Definizione della lista di ingredienti
    val ingredients = remember {
        mutableStateListOf(
            "Pomodoro", "Formaggio", "Basilico", "Olive", "Aglio",
            "Cipolla", "Pepe", "Sale", "Olio d'Oliva", "Farina",
            "Uovo", "Latte", "Burro", "Zucchero", "Lievito",
            "Acqua", "Carota", "Sedano", "Peperone", "Melanzana"
        ).map { name -> Ingredient(name) }
    }.toMutableList()
    // Stato per dialogo popup
    var showDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var gramsText by remember { mutableStateOf(TextFieldValue()) }
    var unitText by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current


    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { /* Logica per suggerire ricetta */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF57965C),
                    ),
            ) {
            Text(
                text = "Consigliami una ricetta!",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        }
}
) {
    paddingValues ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(ingredients) { index, ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = ingredient.isChecked,
                    onCheckedChange = { checked ->
                        ingredients[index] = ingredient.copy(isChecked = checked)
                        if (checked) {
                            selectedIndex = index
                            gramsText = TextFieldValue()
                            showDialog = true
                        } else {
                            // Se deselezionato, reset del peso
                            ingredients[index] = ingredient.copy(isChecked = false, grams = "")
                        }
                    }
                )
                Text(
                    text = ingredient.name + if (ingredient.grams?.isNotEmpty() == true) " (${ingredient.grams} g)" else "",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    // Dialog per inserimento grammi
    if (showDialog && selectedIndex >= 0) {
        AlertDialog(
            onDismissRequest = {
                // Chiudi e reset se si tocca fuori
                showDialog = false
                ingredients[selectedIndex] = ingredients[selectedIndex].copy(isChecked = false)
            },
            title = {
                Text(
                    text = "Quantità di ${ingredients[selectedIndex].name}?",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = gramsText,
                        onValueChange = { gramsText = it },
                        label = { Text(text = "Grammi") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = unitText,
                        onValueChange = { unitText = it },
                        label = { Text(text = "Unità") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if(gramsText.text.isEmpty() && unitText.text.isEmpty()) {
                        Toast.makeText(context, "Inserisci almeno una delle unità di misura", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    ingredients[selectedIndex] =
                        ingredients[selectedIndex].copy(grams = gramsText.text, unit = unitText.text)
                    showDialog = false
                }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    ingredients[selectedIndex] =
                        ingredients[selectedIndex].copy(isChecked = false)
                }) {
                    Text(text = "Annulla")
                }
            }
        )
    }
}
}


@Preview
@Composable
fun RecipesScreenPreview() {
    RecipesScreen()
}