package com.example.foodly

import com.example.foodly.statistics.NutritionalData
import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleNutritionalDataTest {
    @Test
    fun create_nutritional_data_and_assert_values() {
        val data = NutritionalData(
            calories = 100f,
            carbohydrates = 50f,
            fat = 20f,
            fiber = 10f,
            protein = 30f
        )
        assertEquals(100f, data.calories)
        assertEquals(50f, data.carbohydrates)
        assertEquals(20f, data.fat)
        assertEquals(10f, data.fiber)
        assertEquals(30f, data.protein)
    }
}
