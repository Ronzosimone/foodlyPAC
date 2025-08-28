package com.example.foodly.statistics

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.Result
import com.example.foodly.api.StatisticsApiClient
import com.example.foodly.api.response.NutritionalAverage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel : ViewModel() {

    // Dati per il grafico a torta (macronutrienti)
    private val _nutritionalData = MutableStateFlow<NutritionalData?>(null)
    val nutritionalData: StateFlow<NutritionalData?> = _nutritionalData.asStateFlow()

    // Healthy score
    private val _healthyScoreData = MutableStateFlow(0f)
    val healthyScoreData: StateFlow<Float> = _healthyScoreData.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadStatistics(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                when (val result = StatisticsApiClient.getRecipesStatistics(context)) {
                    is Result.Success -> {
                        result.data.let { stats ->
                            _nutritionalData.value = NutritionalData(
                                calories = stats.average.calories.toFloat(),
                                carbohydrates = stats.average.carbohydrates.toFloat(),
                                fat = stats.average.fat.toFloat(),
                                fiber = stats.average.fiber.toFloat(),
                                protein = stats.average.protein.toFloat()
                            )
                            _healthyScoreData.value = stats.average.healty_score.toFloat()
                        }
                        Log.d("StatisticsViewModel", "Statistics loaded successfully")
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                        Log.e("StatisticsViewModel", "Error loading statistics: ${result.message}")
                        // Fallback ai dati mock in caso di errore
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errore durante il caricamento delle statistiche"
                Log.e("StatisticsViewModel", "Exception loading statistics", e)
                // Fallback ai dati mock in caso di eccezione
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun clearError() {
        _errorMessage.value = null
    }
}

data class NutritionalData(
    val calories: Float,
    val carbohydrates: Float,
    val fat: Float,
    val fiber: Float,
    val protein: Float
)
