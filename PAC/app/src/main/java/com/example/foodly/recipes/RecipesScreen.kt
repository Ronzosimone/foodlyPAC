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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // For placeholder/error drawables if available
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodly.R // Assuming you might have placeholder/error drawables in res
import com.example.foodly.backend.Recipe // Ensure this import is correct
import com.example.foodly.ui.theme.FoodlyTheme // Import your custom theme

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
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            text = "No recipes found. Try refreshing!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "Error: ${state.message}\nTap to retry.", // Consider making retry clickable
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is RecipesUiState.Idle -> {
                 Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        "Welcome! Let's find some recipes.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.image)
                    .crossfade(true)
                    // Placeholder and error can be generic or from R.drawable if you add them
                    // .placeholder(painterResource(id = R.drawable.placeholder_image))
                    // .error(painterResource(id = R.drawable.error_image))
                    .build(),
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                // Fallback for preview or if you don't have drawable resources yet
                onLoading = { Text("Loading...", modifier = Modifier.align(Alignment.Center)) },
                onError = { Text("Image failed to load.", modifier = Modifier.align(Alignment.Center)) }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Likes: ${recipe.likes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Missed Ingredients: ${recipe.missedIngredientCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // You can expand this to show a snippet of missedIngredients if desired
            }
        }
    }
}

@Preview(showBackground = true, name = "Recipe Item Preview")
@Composable
fun RecipeItemPreview() {
    FoodlyTheme { // Use your custom theme for preview
        RecipeItem(
            recipe = Recipe(
                id = 715415,
                image = "https://img.spoonacular.com/recipes/715415-312x231.jpg", // A real image URL
                imageType = "jpg",
                likes = 120,
                missedIngredientCount = 3,
                missedIngredients = emptyList(),
                title = "Delicious Red Lentil Soup with Chicken and Turnips",
                unusedIngredients = emptyList(),
                usedIngredientCount = 5,
                usedIngredients = emptyList()
            )
        )
    }
}

@Preview(showBackground = true, name = "Recipes Screen Success")
@Composable
fun RecipesScreenPreview_Success() {
    FoodlyTheme {
        // Mocking a success state for preview
        val recipes = listOf(
            Recipe(id = 1, title = "Spaghetti Bolognese", image = "url1", imageType = "jpg", likes = 150, missedIngredientCount = 2, missedIngredients = emptyList(), unusedIngredients = emptyList(), usedIngredientCount = 3, usedIngredients = emptyList()),
            Recipe(id = 2, title = "Chicken Curry", image = "url2", imageType = "jpg", likes = 200, missedIngredientCount = 1, missedIngredients = emptyList(), unusedIngredients = emptyList(), usedIngredientCount = 4, usedIngredients = emptyList())
        )
        // This preview won't show the ViewModel states directly, but you can mock the UI state
        // For a more direct preview of states, you'd pass the state to RecipesScreenContent composable
        Surface(modifier = Modifier.fillMaxSize()) {
             LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recipes) { recipe ->
                    RecipeItem(recipe = recipe)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen Error")
@Composable
fun RecipesScreenPreview_Error() {
    FoodlyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Error: Failed to load recipes.\nTap to retry.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen Empty")
@Composable
fun RecipesScreenPreview_Empty() {
    FoodlyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "No recipes found. Try refreshing!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}