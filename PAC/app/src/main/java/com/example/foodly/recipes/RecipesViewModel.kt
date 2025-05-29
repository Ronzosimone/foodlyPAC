package com.example.foodly.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.backend.Recipe // Assuming RecipeData.kt is accessible
import com.example.foodly.data.network.RecipeService
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
                val recipes = RecipeService.getRecipes()
                if (recipes.isNotEmpty()) {
                    _uiState.value = RecipesUiState.Success(recipes)
                } else {
                    // Could be an error or simply no recipes from the backend mock
                    _uiState.value = RecipesUiState.Error("No recipes found or error fetching.")
                }
            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error("Failed to fetch recipes: ${e.message}")
            }
        }
    }
}
