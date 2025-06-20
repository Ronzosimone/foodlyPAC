package com.example.foodly.recipes

import com.example.foodly.data.mock.MockRecipeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipesViewModelTest {

    // Rule to set the main dispatcher for tests
    // Using a TestCoroutineScheduler and StandardTestDispatcher
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state and successful data loading`() = runTest(testDispatcher) {
        // ViewModel calls fetchRecipes in init
        val viewModel = RecipesViewModel()

        // Advance past the initial loading
        // The coroutine in fetchRecipes should execute with runCurrent or advanceUntilIdle
        advanceUntilIdle() // Ensures all coroutines on the testDispatcher have completed

        val uiState = viewModel.uiState.value
        assertTrue("UI state should be Success", uiState is RecipesUiState.Success)

        val recipesInState = (uiState as RecipesUiState.Success).recipes
        assertEquals("Recipes should match mock data", MockRecipeData.recipes, recipesInState)
        assertTrue("Recipes list should not be empty", recipesInState.isNotEmpty())
    }

    @Test
    fun `fetchRecipes updates state to Success with mock data`() = runTest(testDispatcher) {
        val viewModel = RecipesViewModel()
        // Initial fetch is done in init, advance past it
        advanceUntilIdle()

        // Call fetchRecipes again (e.g., for a refresh scenario)
        viewModel.fetchRecipes()

        // First it should go to Loading
        var currentState = viewModel.uiState.value
        assertTrue("UI state should be Loading after fetchRecipes call", currentState is RecipesUiState.Loading)

        // Advance coroutines to completion
        advanceUntilIdle()

        currentState = viewModel.uiState.value
        assertTrue("UI state should be Success after fetchRecipes completes", currentState is RecipesUiState.Success)
        
        val recipesInState = (currentState as RecipesUiState.Success).recipes
        assertEquals("Recipes should match mock data on subsequent fetch", MockRecipeData.recipes, recipesInState)
        assertTrue("Recipes list should not be empty on subsequent fetch", recipesInState.isNotEmpty())
    }
    
    // Optional: Test for empty data scenario if MockRecipeData could be modified for testing
    // This would require a way to provide a different version of MockRecipeData or
    // temporarily altering its content, which is more involved.
    // For now, we assume MockRecipeData.recipes is not empty as per current setup.
    // @Test
    // fun `fetchRecipes with empty mock data results in Error state`() = runTest {
    //     // Setup: Modify MockRecipeData.recipes to be empty (requires test-specific DI or reflection)
    //     val originalRecipes = MockRecipeData.recipes
    //     // val mockDataField = MockRecipeData::class.java.getDeclaredField("recipes")
    //     // ... make it accessible and set to emptyList()
    //
    //     val viewModel = RecipesViewModel()
    //     advanceUntilIdle() // For init call
    //
    //     val uiState = viewModel.uiState.value
    //     assertTrue("UI state should be Error for empty data", uiState is RecipesUiState.Error)
    //     assertEquals("Error message for no recipes", "No recipes found in mock data.", (uiState as RecipesUiState.Error).message)
    //
    //     // Teardown: Restore MockRecipeData.recipes
    //     // ... set mockDataField back to originalRecipes
    // }
}
