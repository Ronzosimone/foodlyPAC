package com.example.foodly.recipes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder // Or Favorite for filled heart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.foodly.backend.Recipe // Assuming Recipe is in backend package
import com.example.foodly.ui.theme.FoodlyTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.example.foodly.backend.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow

// Mock Recipe Data (align with the structure in RecipeData.kt)
// This can be removed if the screen exclusively relies on ViewModel passed data
// For preview purposes, it's still useful.
val previewMockRecipe = Recipe(
    id = 716429,
    title = "Preview: Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
    image = "https://img.spoonacular.com/recipes/716429-556x370.jpg",
    imageType = "jpg",
    likes = 150,
    missedIngredientCount = 2,
    missedIngredients = listOf(
        com.example.foodly.backend.Ingredient( name = "Cauliflower", amount = 1.0, unit = "head", original = "1 head cauliflower", image = "cauliflower.jpg", extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta", id = 1003),
        com.example.foodly.backend.Ingredient( name = "Scallions", amount = 1.0, unit = "bunch", original = "1 bunch scallions", image = "scallions.jpg", id = 1002, extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta")
    ),
    usedIngredientCount = 0,
    usedIngredients = listOf(
        com.example.foodly.backend.Ingredient(id = 1003, name = "Pasta", amount = 1.0, unit = "lb", original = "1 lb pasta", image = "pasta.jpg", extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta"),
        com.example.foodly.backend.Ingredient(id = 1004, name = "Garlic", amount = 4.0, unit = "cloves", original = "4 cloves garlic", image = "garlic.jpg",extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta"),
        com.example.foodly.backend.Ingredient(id = 1005, name = "Breadcrumbs", amount = 0.5, unit = "cup", original = "1/2 cup breadcrumbs", image = "breadcrumbs.jpg",extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta"),
        com.example.foodly.backend.Ingredient(id = 1006, name = "Olive Oil", amount = 2.0, unit = "tbsp", original = "2 tbsp olive oil", image = "oliveoil.jpg",extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta"),
        com.example.foodly.backend.Ingredient(id = 1007, name = "Salt", amount = 1.0, unit = "tsp", original = "1 tsp salt", image = "salt.jpg",extendedName = "spaghetti", meta = listOf("spaghetti"), originalName = "spaghetti pasta")
    ),
    unusedIngredients = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipesViewModel: RecipesViewModel = viewModel(), // Inject ViewModel
    onNavigateBack: () -> Unit
) {
    val uiState by recipesViewModel.uiState.collectAsState()
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    when (val state = uiState) {
        is RecipesUiState.Loading -> {
            Scaffold(containerColor = Color.Transparent) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        is RecipesUiState.Error -> {
            Scaffold(containerColor = Color.Transparent) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        is RecipesUiState.Success -> {
            val recipe = state.recipes.find { it.id == recipeId }

            if (recipe == null) {
                Scaffold(containerColor = Color.Transparent) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(gradient)
                            .padding(paddingValues)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Recipe not found.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                return // Exit if recipe not found
            }

            // Recipe found, display details
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(recipe.title, maxLines = 2, style = MaterialTheme.typography.titleLarge) }, // Allow 2 lines for title
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(paddingValues)
                ) {
                    // Image
                    item {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current).data(data = recipe.image)
                                    .apply(block = fun ImageRequest.Builder.() {
                                        crossfade(true)
                                    }).build()
                            ),
                            contentDescription = recipe.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Likes Section
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Likes",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${recipe.likes} likes",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    item { Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                    // Used Ingredients Section
                    if (recipe.usedIngredients.isNotEmpty()) {
                        item {
                            IngredientSection(
                                title = "Used Ingredients (${recipe.usedIngredientCount})",
                                ingredients = recipe.usedIngredients
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Missed Ingredients Section
                    if (recipe.missedIngredients.isNotEmpty()) {
                        item {
                            IngredientSection(
                                title = "Missed Ingredients (${recipe.missedIngredientCount})",
                                ingredients = recipe.missedIngredients,
                                cardColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
        is RecipesUiState.Idle -> { // Handle Idle state, perhaps show loading or nothing
            Scaffold(containerColor = Color.Transparent) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    // Could also be an empty Box or a specific message for Idle
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun IngredientSection(
    title: String,
    ingredients: List<Ingredient>,
    cardColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ingredients.forEachIndexed { index, ingredient ->
                    Text(
                        text = ingredient.original
                            ?: "${ingredient.amount} ${ingredient.unit} ${ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                    if (index < ingredients.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = contentColor.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview(showBackground = true, name = "Recipe Detail Screen Preview")
@Composable
fun RecipeDetailScreenPreview() {
    FoodlyTheme {
        // Use the previewMockRecipe for the preview
        val mockViewModel = RecipesViewModel() // This will initialize with mock data
        // Simulate a success state for the preview
        (mockViewModel.uiState as? MutableStateFlow<RecipesUiState>)?.value = RecipesUiState.Success(listOf(previewMockRecipe))

        RecipeDetailScreen(
            recipeId = previewMockRecipe.id,
            recipesViewModel = mockViewModel,
            onNavigateBack = {}
        )
    }
}
