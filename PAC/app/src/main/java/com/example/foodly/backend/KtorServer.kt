package com.example.foodly.backend

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
        Ingredient(
            amount = 8.0,
            extendedName = "diced additional toppings avocado",
            id = 9037,
            image = "https://img.spoonacular.com/ingredients_100x100/avocado.jpg",
            meta = listOf("diced", "chopped"),
            name = "additional toppings avocado",
            original = "additional toppings: diced avocado, micro greens, chopped basil)",
            originalName = "additional toppings: diced avocado, micro greens, chopped basil)",
            unit = "servings",
            unitLong = "servings",
            unitShort = "servings"
        )
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
        Ingredient(
            amount = 1.0,
            extendedName = "frozen asparagus",
            id = 11011,
            image = "https://img.spoonacular.com/ingredients_100x100/asparagus.png",
            meta = listOf("frozen", "organic", "thawed", "(preferably )"),
            name = "asparagus",
            original = "1 bag of frozen organic asparagus (preferably thawed)",
            originalName = "frozen organic asparagus (preferably thawed)",
            unit = "bag",
            unitLong = "bag",
            unitShort = "bag"
        )
        // ... (add other missed ingredients for full representation if needed for client)
    ),
    title = "Asparagus and Cauliflower Salad with Miso Dressing",
    unusedIngredients = listOf(
        Ingredient(
            amount = 1.0,
            extendedName = null,
            id = 16112,
            image = "https://img.spoonacular.com/ingredients_100x100/miso.jpg",
            meta = listOf("white"),
            name = "miso",
            original = "1 tbsp white miso",
            originalName = "white miso",
            unit = "tbsp",
            unitLong = "tablespoon",
            unitShort = "tbsp"
        )
    ),
    usedIngredientCount = 0,
    usedIngredients = emptyList()
)



