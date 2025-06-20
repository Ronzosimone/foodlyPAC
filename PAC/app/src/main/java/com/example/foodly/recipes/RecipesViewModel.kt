package com.example.foodly.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.backend.Recipe // Assuming RecipeData.kt is accessible
import com.example.foodly.data.mock.MockRecipeData // Import mock data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RecipesUiState {
    object Loading : RecipesUiState
    data class Success(val recipes: List<Recipe>) : RecipesUiState
    data class Error(val message: String) : RecipesUiState
    object Idle : RecipesUiState // Initial state
}

class RecipesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Idle)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        fetchRecipes() // Fetch recipes when ViewModel is created
    }

    fun fetchRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading
            try {
                // Simulate a small delay, similar to network calls (optional but good for UX testing)
                // kotlinx.coroutines.delay(500) 
                
                val recipes = MockRecipeData.recipes // Load from mock
                if (recipes.isNotEmpty()) {
                    _uiState.value = RecipesUiState.Success(recipes)
                } else {
                    _uiState.value = RecipesUiState.Error("No recipes found in mock data.")
                }
            } catch (e: Exception) {
                // This catch block might be less relevant if mock data is statically defined
                // but good to keep for robustness or future changes.
                _uiState.value = RecipesUiState.Error("Failed to load recipes from mock: ${e.message}")
            }
        }
    }
}
