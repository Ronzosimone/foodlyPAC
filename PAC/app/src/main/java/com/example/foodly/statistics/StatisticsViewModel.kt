package com.example.foodly.statistics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatisticsViewModel : ViewModel() {

    private val _weeklyKcalData = MutableStateFlow<Map<String, Float>>(emptyMap())
    val weeklyKcalData: StateFlow<Map<String, Float>> = _weeklyKcalData.asStateFlow()

    private val _healthyScoreData = MutableStateFlow(0f)
    val healthyScoreData: StateFlow<Float> = _healthyScoreData.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // Mock weekly kcal data (e.g., kcal per day)
        _weeklyKcalData.value = mapOf(
            "Lun" to 2150f,
            "Mar" to 1980f,
            "Mer" to 2300f,
            "Gio" to 2050f,
            "Ven" to 2400f,
            "Sab" to 2650f,
            "Dom" to 2200f
        )

        // Mock healthy score
        _healthyScoreData.value = 75.5f
    }
}
