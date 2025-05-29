package com.example.foodly.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.foodly.backend.Recipe
import com.example.foodly.ui.theme.FoodlyTheme

import androidx.navigation.NavController

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipesViewModel = viewModel(),
    navController: NavController // Added NavController parameter
) {
    val uiState by viewModel.uiState.collectAsState()

    // Use MaterialTheme.colorScheme.background for the main screen background
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val state = uiState) {
            is RecipesUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is RecipesUiState.Success -> {
                if (state.recipes.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp) // Consistent padding
                    ) {
                        Text(
                            text = "No recipes found. Try refreshing!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface, // Use onSurface for better contrast on background
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(all = 16.dp), // Consistent padding
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Consistent spacing
                    ) {
                        items(state.recipes) { recipe ->
                            RecipeItem(
                                recipe = recipe,
                                // Pass navigation callback to RecipeItem
                                onRecipeClick = {
                                    navController.navigate(com.example.foodly.home.HomeNavRoutes.createRecipeDetailRoute(recipe.id))
                                }
                            )
                        }
                    }
                }
            }
            is RecipesUiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp) // Consistent padding
                ) {
                    Text(
                        text = "Error: ${state.message}\nTap to retry.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error, // Error color is appropriate here
                        textAlign = TextAlign.Center
                    )
                }
            }
            is RecipesUiState.Idle -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp) // Consistent padding
                ) {
                    Text(
                        "Welcome! Let's find some recipes.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface, // Use onSurface for better contrast
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    onRecipeClick: () -> Unit // Callback for click event
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRecipeClick() }, // Make the Card clickable
        // M3 often uses lower elevation or filled styles.
        // Keep elevation for now, but ensure it's visually harmonious.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Slightly reduced elevation
        // Use surface for cards to make them distinct from background, surfaceVariant for less emphasis.
        // The new theme's surface should work well.
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            RecipeImage(
                imageUrl = recipe.image,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp) // Slightly increased height for better visual
            )
            // Consistent padding within the card content area
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium, // Adjusted for card context
                    color = MaterialTheme.colorScheme.onSurface, // Text on primary surface
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Likes: ${recipe.likes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Secondary info
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Missed Ingredients: ${recipe.missedIngredientCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Secondary info
                )
            }
        }
    }
}

@Composable
fun RecipeImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            // Consider adding placeholder/error drawables if you have them
            // .placeholder(R.drawable.placeholder_image)
            // .error(R.drawable.error_image)
            .build()
    )
    val state = painter.state

    Box(modifier = modifier, contentAlignment = Alignment.Center) { // Center content like progress/error
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop, // Crop is good for filling space
            modifier = Modifier.matchParentSize()
        )

        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box( // Ensure this Box fills the image area
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)), // Use surface with alpha
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is AsyncImagePainter.State.Error -> {
                Box( // Ensure this Box fills the image area
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)), // Use surfaceVariant
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image failed to load.",
                        style = MaterialTheme.typography.labelMedium, // Smaller text for overlay
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp) // Smaller padding for overlay
                    )
                }
            }
            else -> Unit // State.Success or State.Empty
        }
    }
}

@Preview(showBackground = true, name = "Recipe Item Preview")
@Composable
fun RecipeItemPreview() {
    FoodlyTheme {
        RecipeItem( // Update preview to reflect new parameter, even if with empty lambda
            recipe = Recipe(
                id = 715415,
                image = "https://img.spoonacular.com/recipes/715415-312x231.jpg",
                imageType = "jpg",
                likes = 120,
                missedIngredientCount = 3,
                missedIngredients = emptyList(),
                title = "Delicious Red Lentil Soup with Chicken and Turnips",
                unusedIngredients = emptyList(),
                usedIngredientCount = 5,
                usedIngredients = emptyList()
            ),
            onRecipeClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Recipes Screen Success")
@Composable
fun RecipesScreenPreview_Success() {
    FoodlyTheme {
        val recipes = listOf(
            Recipe(
                id = 1, title = "Spaghetti Bolognese",
                image = "url1", imageType = "jpg",
                likes = 150, missedIngredientCount = 2,
                missedIngredients = emptyList(),
                unusedIngredients = emptyList(),
                usedIngredientCount = 3,
                usedIngredients = emptyList()
            ),
            Recipe(
                id = 2, title = "Chicken Curry",
                image = "url2", imageType = "jpg",
                likes = 200, missedIngredientCount = 1,
                missedIngredients = emptyList(),
                unusedIngredients = emptyList(),
                usedIngredientCount = 4,
                usedIngredients = emptyList()
            )
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeItem(recipe = recipe, onRecipeClick = {}) // Update preview
            }
        }
    }
}