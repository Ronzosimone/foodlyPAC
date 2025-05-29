package com.example.foodly.backend

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

val recipeJsonString = """
{
  "recipes": {
    "number": 100,
    "offset": 0,
    "results": [
      {
        "id": 715415,
        "image": "https://img.spoonacular.com/recipes/715415-312x231.jpg",
        "imageType": "jpg",
        "likes": 0,
        "missedIngredientCount": 11,
        "missedIngredients": [
          {
            "aisle": "Produce",
            "amount": 8.0,
            "extendedName": "diced additional toppings avocado",
            "id": 9037,
            "image": "https://img.spoonacular.com/ingredients_100x100/avocado.jpg",
            "meta": ["diced", "chopped"],
            "name": "additional toppings avocado",
            "original": "additional toppings: diced avocado, micro greens, chopped basil)",
            "originalName": "additional toppings: diced avocado, micro greens, chopped basil)",
            "unit": "servings",
            "unitLong": "servings",
            "unitShort": "servings"
          }
        ],
        "title": "Red Lentil Soup with Chicken and Turnips",
        "unusedIngredients": [],
        "usedIngredientCount": 0,
        "usedIngredients": []
      },
      {
        "id": 716406,
        "image": "https://img.spoonacular.com/recipes/716406-312x231.jpg",
        "imageType": "jpg",
        "likes": 0,
        "missedIngredientCount": 5,
        "missedIngredients": [
          {
            "aisle": "Produce",
            "amount": 1.0,
            "extendedName": "frozen asparagus",
            "id": 11011,
            "image": "https://img.spoonacular.com/ingredients_100x100/asparagus.png",
            "meta": ["frozen", "organic", "thawed", "(preferably )"],
            "name": "asparagus",
            "original": "1 bag of frozen organic asparagus (preferably thawed)",
            "originalName": "frozen organic asparagus (preferably thawed)",
            "unit": "bag",
            "unitLong": "bag",
            "unitShort": "bag"
          }
        ],
        "title": "Asparagus and Cauliflower Salad with Miso Dressing",
        "unusedIngredients": [
          {
            "aisle": "Condiments",
            "amount": 1.0,
            "id": 16112,
            "image": "https://img.spoonacular.com/ingredients_100x100/miso.jpg",
            "meta": ["white"],
            "name": "miso",
            "original": "1 tbsp white miso",
            "originalName": "white miso",
            "unit": "tbsp",
            "unitLong": "tablespoon",
            "unitShort": "tbsp"
          }
        ],
        "usedIngredientCount": 0,
        "usedIngredients": []
      }
    ]
  }
}
"""
// In-memory store for user pantries
val userPantries = ConcurrentHashMap<String, List<UserPantryItemRequest>>()

// Hardcoded recipes for recommendation
val recipeRedLentilSoup = Recipe(
    id = 715415,
    image = "https://img.spoonacular.com/recipes/715415-312x231.jpg",
    imageType = "jpg",
    likes = 0,
    missedIngredientCount = 11,
    missedIngredients = listOf(
        Ingredient(aisle = "Produce", amount = 8.0, extendedName = "diced additional toppings avocado", id = 9037, image = "https://img.spoonacular.com/ingredients_100x100/avocado.jpg", meta = listOf("diced", "chopped"), name = "additional toppings avocado", original = "additional toppings: diced avocado, micro greens, chopped basil)", originalName = "additional toppings: diced avocado, micro greens, chopped basil)", unit = "servings", unitLong = "servings", unitShort = "servings")
        // ... (add other missed ingredients for full representation if needed for client)
    ),
    title = "Red Lentil Soup with Chicken and Turnips",
    unusedIngredients = emptyList(),
    usedIngredientCount = 0,
    usedIngredients = emptyList()
)

val recipeAsparagusSalad = Recipe(
    id = 716406,
    image = "https://img.spoonacular.com/recipes/716406-312x231.jpg",
    imageType = "jpg",
    likes = 0,
    missedIngredientCount = 5,
    missedIngredients = listOf(
        Ingredient(aisle = "Produce", amount = 1.0, extendedName = "frozen asparagus", id = 11011, image = "https://img.spoonacular.com/ingredients_100x100/asparagus.png", meta = listOf("frozen", "organic", "thawed", "(preferably )"), name = "asparagus", original = "1 bag of frozen organic asparagus (preferably thawed)", originalName = "frozen organic asparagus (preferably thawed)", unit = "bag", unitLong = "bag", unitShort = "bag")
        // ... (add other missed ingredients for full representation if needed for client)
    ),
    title = "Asparagus and Cauliflower Salad with Miso Dressing",
    unusedIngredients = listOf(
        Ingredient(aisle = "Condiments", amount = 1.0, id = 16112, image = "https://img.spoonacular.com/ingredients_100x100/miso.jpg", meta = listOf("white"), name = "miso", original = "1 tbsp white miso", originalName = "white miso", unit = "tbsp", unitLong = "tablespoon", unitShort = "tbsp", extendedName = null, originalName = null)
    ),
    usedIngredientCount = 0,
    usedIngredients = emptyList()
)


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        routing {
            get("/recipes") {
                try {
                    val root = Json.decodeFromString<RootRecipes>(recipeJsonString)
                    call.respond(root.recipes.results)
                } catch (e: Exception) {
                    application.log.error("Failed to parse recipes or respond", e)
                    call.respondText("Error processing request: ${e.localizedMessage}")
                }
            }

            // Pantry Update Endpoint
            post("/pantry/{userId}") {
                val userId = call.parameters["userId"] ?: "defaultUser"
                try {
                    val pantryItems = call.receive<List<UserPantryItemRequest>>()
                    userPantries[userId] = pantryItems
                    application.log.info("Pantry updated for user $userId: $pantryItems")
                    call.respond(ResponseMessage("Pantry updated successfully for user $userId"))
                } catch (e: Exception) {
                    application.log.error("Failed to update pantry for user $userId", e)
                    call.respond(ResponseMessage("Error updating pantry: ${e.localizedMessage}"))
                }
            }

            // Recipe Recommendation Endpoint
            post("/recommendations/{userId}") {
                val userId = call.parameters["userId"] ?: "defaultUser" // Placeholder
                try {
                    val pantryItems = call.receive<List<UserPantryItemRequest>>()
                    application.log.info("Received pantry for recommendation for $userId: $pantryItems")

                    val hasChicken = pantryItems.any { it.name.equals("Chicken Breast", ignoreCase = true) }

                    val recommendedRecipes = if (hasChicken) {
                        listOf(recipeRedLentilSoup)
                    } else {
                        listOf(recipeAsparagusSalad)
                    }
                    call.respond(recommendedRecipes)

                } catch (e: Exception) {
                    application.log.error("Failed to get recommendations for user $userId", e)
                    call.respond(ResponseMessage("Error getting recommendations: ${e.localizedMessage}"))
                }
            }
        }
    }.start(wait = true)
}
