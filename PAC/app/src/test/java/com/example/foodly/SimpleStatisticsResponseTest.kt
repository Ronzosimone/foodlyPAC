package com.example.foodly

import com.example.foodly.api.response.NutritionalAverage
import com.example.foodly.api.response.StatisticsResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SimpleStatisticsResponseTest {
    @Test
    fun statistics_response_defaults_are_null() {
        val response = StatisticsResponse()
        assertNull(response.average)
        assertNull(response.id_user)
        assertNull(response.recipes)
    }

    @Test
    fun statistics_response_assign_and_assert_fields() {
        val avg = NutritionalAverage(
            calories = 2000.0,
            carbohydrates = 250.0,
            fat = 80.0,
            fiber = 30.0,
            healty_score = 90.0,
            protein = 120.0,
            saturated_fat = 25.0
        )
        val response = StatisticsResponse(
            average = avg,
            id_user = "user123",
            recipes = 10
        )
        assertEquals("user123", response.id_user)
        assertEquals(10, response.recipes)
        assertEquals(2000.0, response.average?.calories)
        assertEquals(90.0, response.average?.healty_score)
    }
}
