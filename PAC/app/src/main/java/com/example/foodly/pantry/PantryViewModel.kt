package com.example.foodly.pantry

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserPantryItem(
    val pantryIngredient: PantryIngredientItem, // The selected predefined ingredient
    var quantity: Double,
    var unit: String
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

    fun addPantryItem(item: PantryIngredientItem, quantity: Double, unit: String) {
        val newItem = UserPantryItem(item, quantity, unit)
        _userPantryItems.update { currentList ->
            // Check if item already exists, if so, update it (optional, or allow duplicates)
            // For now, let's assume we add as new or replace if ID matches.
            // A more robust approach might be to not allow adding if ID exists, force update.
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
}
