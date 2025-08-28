package com.example.foodly.recipes

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.foodly.backend.Ingredient
import com.example.foodly.backend.Recipe
import com.example.foodly.ui.theme.FoodlyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

// Helper function to convert recipe title to Spoonacular URL format
fun createSpoonacularUrl(title: String, id: Int): String {
    val formattedTitle = title
        .lowercase(Locale.getDefault())
        .replace(Regex("[^a-z0-9\\s]"), "") // Remove special characters
        .replace(Regex("\\s+"), "-") // Replace spaces with dashes
        .trim('-') // Remove leading/trailing dashes
    
    return "https://spoonacular.com/$formattedTitle-$id"
}

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

    when (val state = uiState) {
        is RecipesUiState.Loading -> {
            Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        is RecipesUiState.Error -> {
            Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
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
                Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
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
                        title = { Text("Recipe Detail", maxLines = 1, style = MaterialTheme.typography.titleLarge) }, // Allow 2 lines for title
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                        SpoonacularWebView(
                            recipe = recipe,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )


                    //item { Spacer(modifier = Modifier.height(16.dp)) }

                    // Used Ingredients Section (keeping as backup info)
                   /* if (recipe.usedIngredients.isNotEmpty()) {
                        item {
                            IngredientSection(
                                title = "Ingredienti Utilizzati (${recipe.usedIngredientCount})",
                                ingredients = recipe.usedIngredients
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Missed Ingredients Section (keeping as backup info)
                    if (recipe.missedIngredients.isNotEmpty()) {
                        item {
                            IngredientSection(
                                title = "Altri Ingredienti (${recipe.missedIngredientCount})",
                                ingredients = recipe.missedIngredients,
                                cardColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }*/
                }
            }
        }
        is RecipesUiState.Idle -> { // Handle Idle state, perhaps show loading or nothing
            Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    // Could also be an empty Box or a specific message for Idle
                    CircularProgressIndicator() 
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SpoonacularWebView(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    val spoonacularUrl = createSpoonacularUrl(recipe.title, recipe.id)
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        
                        // JavaScript to show only the wrapper div with class row
                        val javascript = """
                            javascript:(function() {
                                // Hide everything first
                                document.body.style.display = 'none';
                                
                                // Find the wrapper element
                                var wrapper = document.getElementById('wrapper');
                                if (wrapper) {
                                    // Create a new body content with only the wrapper
                                    var newBody = document.createElement('div');
                                    newBody.appendChild(wrapper.cloneNode(true));
                                    
                                    // Clear the body and add only our content
                                    document.body.innerHTML = '';
                                    document.body.appendChild(newBody);
                                    
                                    // Show the body again
                                    document.body.style.display = 'block';
                                    
                                    // Additional styling to make it look better in our WebView
                                    var style = document.createElement('style');
                                    style.textContent = `
                                        body { 
                                            margin: 0; 
                                            padding: 10px; 
                                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                        }
                                        .row { 
                                            max-width: 100% !important; 
                                            margin: 0 !important;
                                        }
                                        img { 
                                            max-width: 100% !important; 
                                            height: auto !important; 
                                        }
                                        .recipe-summary { 
                                            font-size: 14px !important; 
                                        }
                                        .recipe-instructions { 
                                            font-size: 14px !important; 
                                        }
                                        .ingredients-section { 
                                            font-size: 14px !important; 
                                        }
                                        h1, h2, h3 { 
                                            font-size: 18px !important; 
                                            margin: 10px 0 !important; 
                                        }
                                    `;
                                    document.head.appendChild(style);
                                } else {
                                    // Fallback: show the main content area
                                    var mainContent = document.querySelector('.recipe-container, .main-content, main');
                                    if (mainContent) {
                                        document.body.innerHTML = '';
                                        document.body.appendChild(mainContent.cloneNode(true));
                                        document.body.style.display = 'block';
                                    } else {
                                        // If we can't find specific content, just show everything
                                        document.body.style.display = 'block';
                                    }
                                }
                            })();
                        """.trimIndent()
                        
                        view?.evaluateJavascript(javascript, null)
                    }
                }
                
                loadUrl(spoonacularUrl)
            }
        }
    )
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
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
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
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = contentColor.copy(alpha = 0.2f))
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
