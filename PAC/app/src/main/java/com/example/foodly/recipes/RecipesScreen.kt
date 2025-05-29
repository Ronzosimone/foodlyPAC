package com.example.foodly.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.backend.Recipe // Ensure this import is correct

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is RecipesUiState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is RecipesUiState.Success -> {
                if (state.recipes.isEmpty()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("No recipes found. Try refreshing.", fontSize = 18.sp)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.recipes) { recipe ->
                            RecipeItem(recipe = recipe)
                        }
                    }
                }
            }
            is RecipesUiState.Error -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Error: ${state.message}\nTap to retry.",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                        // TODO: Add a click listener to retry viewModel.fetchRecipes()
                    )
                }
            }
            is RecipesUiState.Idle -> {
                 Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Welcome! Let's find some recipes.", fontSize = 18.sp)
                    // Or trigger fetch if not done in init
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), // Ensure card wraps content or has min height
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Placeholder for Image - Coil/Glide would go here
            // For now, just display the image URL as text if you want.
            // Image(painter = rememberAsyncImagePainter(recipe.image), contentDescription = recipe.title)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Fixed height for image placeholder
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Image URL: ${recipe.image}", fontSize = 12.sp)
            }

            Text(
                text = recipe.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Add more details if needed, e.g., missed ingredients count
            Text(
                text = "Missed Ingredients: ${recipe.missedIngredientCount}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Further details like ingredients list can be added here
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesScreenPreview_Success() {
    MaterialTheme {
        // Provide a mock ViewModel or directly test with Success state
        RecipesScreen() // This will show Idle or Loading by default in preview
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeItemPreview() {
    MaterialTheme {
        RecipeItem(
            recipe = Recipe(
                id = 715415,
                image = "https://img.spoonacular.com/recipes/715415-312x231.jpg",
                imageType = "jpg",
                likes = 0,
                missedIngredientCount = 11,
                missedIngredients = emptyList(), // Simplified for preview
                title = "Red Lentil Soup with Chicken and Turnips",
                unusedIngredients = emptyList(),
                usedIngredientCount = 0,
                usedIngredients = emptyList()
            )
        )
    }
}