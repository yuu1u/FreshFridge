package com.example.freshfridge.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class RecipeRepository(
    private val context: Context
) {

    /**
     * 从 assets/recipes.json 加载全部菜谱
     */
    fun loadRecipes(): List<LocalRecipe> {

        return try {

            val jsonString = context.assets
                .open("recipes.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(jsonString)

            val recipes = mutableListOf<LocalRecipe>()

            for (i in 0 until jsonArray.length()) {

                val item = jsonArray.getJSONObject(i)

                val ingredients = parseIngredients(
                    item.getJSONArray("ingredients")
                )

                val steps = parseStringArray(
                    item.getJSONArray("steps")
                )

                val tags = parseStringArray(
                    item.getJSONArray("tags")
                )

                val recipe = LocalRecipe(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    category = item.getString("category"),
                    description = item.getString("description"),
                    ingredients = ingredients,
                    steps = steps,
                    cookingTime = item.getInt("cookingTime"),
                    difficulty = item.getString("difficulty"),
                    servings = item.getInt("servings"),
                    tags = tags
                )

                recipes.add(recipe)
            }

            recipes

        } catch (e: Exception) {

            e.printStackTrace()

            emptyList()
        }
    }

    /**
     * 解析菜谱食材
     */
    private fun parseIngredients(
        array: JSONArray
    ): List<LocalRecipeIngredient> {

        val result = mutableListOf<LocalRecipeIngredient>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            result.add(
                LocalRecipeIngredient(
                    name = item.getString("name"),
                    amount = item.getString("amount")
                )
            )
        }

        return result
    }

    /**
     * 解析字符串数组
     */
    private fun parseStringArray(
        array: JSONArray
    ): List<String> {

        val result = mutableListOf<String>()

        for (i in 0 until array.length()) {
            result.add(array.getString(i))
        }

        return result
    }

    /**
     * 按分类筛选菜谱
     */
    fun getRecipesByCategory(
        recipes: List<LocalRecipe>,
        category: String
    ): List<LocalRecipe> {

        return recipes.filter {
            it.category == category
        }
    }

    /**
     * 根据名称搜索菜谱
     */
    fun searchRecipes(
        recipes: List<LocalRecipe>,
        keyword: String
    ): List<LocalRecipe> {

        return recipes.filter {
            it.name.contains(keyword, ignoreCase = true)
        }
    }
}