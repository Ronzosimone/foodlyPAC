package com.example.foodly.recipes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.RecipesApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.response.RecipeRecommendation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeRecommendationsUiState {
    object Loading : RecipeRecommendationsUiState()
    data class Success(
        val recipes: List<RecipeRecommendation>,
        val remainingIngredients: Map<String, Double>
    ) : RecipeRecommendationsUiState()
    data class Error(val message: String) : RecipeRecommendationsUiState()
}

class RecipeRecommendationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RecipeRecommendationsUiState>(RecipeRecommendationsUiState.Loading)
    val uiState: StateFlow<RecipeRecommendationsUiState> = _uiState.asStateFlow()

    private val _addRecipeLoading = MutableStateFlow<Int?>(null)
    val addRecipeLoading: StateFlow<Int?> = _addRecipeLoading.asStateFlow()

    private val _addRecipeMessage = MutableStateFlow<String?>(null)
    val addRecipeMessage: StateFlow<String?> = _addRecipeMessage.asStateFlow()

    fun loadGreedyRecipes(context: Context) {
        viewModelScope.launch {
            _uiState.value = RecipeRecommendationsUiState.Loading
            when (val result = RecipesApiClient.getGreedyRecipes(context)) {
                is Result.Success -> {
                    val data = result.data
                    _uiState.value = RecipeRecommendationsUiState.Success(
                        recipes = data.ricette_selezionate,
                        remainingIngredients = data.ingredienti_residui
                    )
                    Log.d("RecipeRecommendationsViewModel", "Successfully loaded ${data.ricette_selezionate.size} recipes")
                }
                is Result.Error -> {
                    _uiState.value = RecipeRecommendationsUiState.Error(result.message)
                    Log.e("RecipeRecommendationsViewModel", "Error loading recipes: ${result.message}")
                }
            }
        }
    }

    fun addSelectedRecipe(context: Context, recipeId: Int) {
        viewModelScope.launch {
            _addRecipeLoading.value = recipeId
            _addRecipeMessage.value = null
            when (val result = RecipesApiClient.addSelectedRecipe(context, recipeId)) {
                is Result.Success -> {
                    _addRecipeMessage.value = result.data ?: "Added recipe successfully"
                }
                is Result.Error -> {
                    _addRecipeMessage.value = result.message
                }
            }
            _addRecipeLoading.value = null
        }
    }
}
