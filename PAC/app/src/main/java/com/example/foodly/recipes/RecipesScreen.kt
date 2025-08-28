package com.example.foodly.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipesViewModel = viewModel(),
    onRecipeClick: ((Int) -> Unit)? = null // Add navigation callback
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Ensure main background color
    ) {
        when (val state = uiState) {
            is RecipesUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator() // Default color is fine, usually primary
                }
            }
            is RecipesUiState.Success -> {
                if (state.recipes.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No recipes found. Try refreshing!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.recipes) { recipe ->
                            RecipeItem(
                                recipe = recipe,
                                onRecipeClick = onRecipeClick
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Error: ${state.message}\nTap to retry.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is RecipesUiState.Idle -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Welcome! Let's find some recipes.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    onRecipeClick: ((Int) -> Unit)? = null // Add navigation callback
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {
            onRecipeClick?.invoke(recipe.id) // Navigate to recipe detail with recipe ID
        }
    ) {
        Column {
            RecipeImage(
                imageUrl = recipe.image,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) // Clip top corners
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface, // Use onSurface
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Likes: ${recipe.likes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface, // Use onSurface
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Missed Ingredients: ${recipe.missedIngredientCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface // Use onSurface
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
            .build()
    )
    val state = painter.state

    Box(modifier = modifier) {
        // Image (visible when loaded)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Overlay for loading or error
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurfaceVariant) // Use onSurfaceVariant
                }
            }
            is AsyncImagePainter.State.Error -> {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image failed to load.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> Unit
        }
    }
}

@Preview(showBackground = true, name = "Recipe Item Preview")
@Composable
fun RecipeItemPreview() {
    FoodlyTheme {
        RecipeItem(
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
            )
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
                RecipeItem(
                    recipe = recipe,
                    onRecipeClick = { /* Preview - no action */ }
                )
            }
        }
    }
}