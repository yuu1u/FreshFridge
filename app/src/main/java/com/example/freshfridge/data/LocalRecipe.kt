package com.example.freshfridge.data

data class LocalRecipeIngredient(
    val name: String,
    val amount: String
)

data class LocalRecipe(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val ingredients: List<LocalRecipeIngredient>,
    val steps: List<String>,
    val cookingTime: Int,
    val difficulty: String,
    val servings: Int,
    val tags: List<String>
)