package com.example.foodly.pantry

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.PantryApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.UnitType
import com.example.foodly.api.response.PantryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserPantryItem(
    val pantryIngredient: PantryIngredientItem, // The selected predefined ingredient
    var quantity: Double,
    var unit: String,
    val apiId: Int? = null // ID dall'API per gli item esistenti
) {
    // Unique ID based on the predefined ingredient's ID for easy updates/removals
    val id: Int get() = pantryIngredient.id
}

class PantryViewModel : ViewModel() {

    // Holds the predefined list of ingredients available for selection
    val predefinedIngredients: List<PantryIngredientItem> = PredefinedIngredients.items

    // Holds the user's current pantry items
    private val _userPantryItems = MutableStateFlow<List<UserPantryItem>>(emptyList())
    val userPantryItems: StateFlow<List<UserPantryItem>> = _userPantryItems.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadPantry(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                when (val result = PantryApiClient.getPantry(context)) {
                    is Result.Success -> {
                        // Converti i PantryItem dell'API in UserPantryItem locali
                        val apiItems = result.data.data ?: emptyList()
                        val userItems = apiItems.mapNotNull { apiItem ->
                            convertApiItemToUserItem(apiItem)
                        }
                        _userPantryItems.value = userItems
                        Log.d("PantryViewModel", "Pantry loaded successfully: ${userItems.size} items")
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                        Log.e("PantryViewModel", "Error loading pantry: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errore durante il caricamento della dispensa"
                Log.e("PantryViewModel", "Exception loading pantry", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun convertApiItemToUserItem(apiItem: PantryItem): UserPantryItem? {
        // Trova l'ingrediente predefinito corrispondente al name_ingredient dell'API
        val predefinedIngredient = predefinedIngredients.find { 
            it.name.lowercase() == apiItem.name_ingredient.lowercase() ||
            it.id == apiItem.id_ingredient
        }

        return if (predefinedIngredient != null) {
            // Usa il default_value dall'API e determina la quantità appropriata
            val (quantity, unit) = getQuantityAndUnit(apiItem)

            UserPantryItem(
                pantryIngredient = predefinedIngredient,
                quantity = quantity,
                unit = unit,
                apiId = apiItem.id
            )
        } else {
            // Se non troviamo l'ingrediente predefinito, creiamo uno temporaneo
            Log.w("PantryViewModel", "Ingrediente non trovato: ${apiItem.name_ingredient}")
            val tempIngredient = PantryIngredientItem(
                id = apiItem.id_ingredient,
                name = apiItem.name_ingredient,
                defaultUnit = apiItem.default_value
            )
            
            val (quantity, unit) = getQuantityAndUnit(apiItem)

            UserPantryItem(
                pantryIngredient = tempIngredient,
                quantity = quantity,
                unit = unit,
                apiId = apiItem.id
            )
        }
    }

    private fun getQuantityAndUnit(apiItem: PantryItem): Pair<Double, String> {
        // Usa il default_value dall'API per determinare quale quantità mostrare
        return when (apiItem.default_value.lowercase()) {
            "g", "grams", "gram" -> {
                if (apiItem.grams > 0) apiItem.grams to "g"
                else 1.0 to "g"
            }
            "units", "unità", "pcs", "pieces" -> {
                if (apiItem.units > 0) apiItem.units.toDouble() to "unità"
                else 1.0 to "unità"
            }
            "cups", "cup" -> {
                if (apiItem.cups > 0) apiItem.cups to "cups"
                else 1.0 to "cups"
            }
            else -> {
                when {
                    apiItem.units > 0 -> apiItem.units.toDouble() to apiItem.default_value
                    apiItem.grams > 0 -> apiItem.grams to apiItem.default_value
                    apiItem.cups > 0 -> apiItem.cups to apiItem.default_value
                    else -> 1.0 to apiItem.default_value
                }
            }
        }
    }

    fun addPantryItem(
        context: Context,
        item: PantryIngredientItem,
        quantity: Double,
        unit: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Determina il tipo di unità
                val unitType = when (unit.lowercase()) {
                    "g", "kg", "grams", "kilograms" -> UnitType.GRAMS
                    else -> UnitType.UNITS
                }

                // Chiama l'API
                when (val result = PantryApiClient.addPantryItem(
                    context = context,
                    ingredientId = item.id,
                    quantity = quantity.toString(),
                    unitType = unitType
                )) {
                    is Result.Success -> {
                        // Successo: ricarica la dispensa dall'API per avere i dati aggiornati
                        Log.d("PantryViewModel", "Pantry item added successfully: ${result.data.message}")
                        loadPantry(context) // Ricarica la dispensa per avere i dati freschi
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                        Log.e("PantryViewModel", "Error adding pantry item: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errore durante l'aggiunta dell'ingrediente"
                Log.e("PantryViewModel", "Exception adding pantry item", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mantieni il metodo locale per compatibilità con il codice esistente
    fun addPantryItemLocal(item: PantryIngredientItem, quantity: Double, unit: String) {
        val newItem = UserPantryItem(item, quantity, unit)
        _userPantryItems.update { currentList ->
            val existingItemIndex = currentList.indexOfFirst { it.id == newItem.id }
            if (existingItemIndex != -1) {
                // Item exists, update it
                currentList.toMutableList().apply {
                    this[existingItemIndex] = newItem
                }
            } else {
                // Item doesn't exist, add it
                currentList + newItem
            }
        }
    }

    fun removePantryItem(itemId: Int) {
        _userPantryItems.update { currentList ->
            currentList.filterNot { it.id == itemId }
        }
    }

    fun updatePantryItem(itemId: Int, newQuantity: Double, newUnit: String) {
        _userPantryItems.update { currentList ->
            currentList.map {
                if (it.id == itemId) {
                    it.copy(quantity = newQuantity, unit = newUnit)
                } else {
                    it
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
