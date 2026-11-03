package com.example.freshfridge

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: Int,
    val difficulty: String
)
