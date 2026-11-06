package com.example.freshfridge.data

import com.example.freshfridge.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AiRecipeApi {

    // Android 模拟器使用这个地址
    // 如果使用真实手机，之后需要改成电脑的局域网 IP
    private const val BASE_URL = "http://10.0.2.2:8000"

    suspend fun recommendRecipes(
        ingredients: List<Ingredient>,
        count: Int = 5
    ): List<LocalRecipe> = withContext(Dispatchers.IO) {

        val url = URL("$BASE_URL/recommend")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
            connection.doOutput = true
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            val ingredientArray = JSONArray()

            ingredients.forEach { ingredient ->
                val item = JSONObject()
                item.put("name", ingredient.name)
                item.put("amount", ingredient.amount + ingredient.unit)
                ingredientArray.put(item)
            }

            val requestBody = JSONObject().apply {
                put("ingredients", ingredientArray)
                put("count", count)
            }

            connection.outputStream.use { output ->
                output.write(requestBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode

            val responseText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use {
                    it.readText()
                }
            } else {
                connection.errorStream?.bufferedReader()?.use {
                    it.readText()
                } ?: "服务器请求失败：$responseCode"
            }

            if (responseCode !in 200..299) {
                throw Exception(responseText)
            }

            val response = JSONObject(responseText)

            if (!response.optBoolean("success", false)) {
                throw Exception(
                    response.optString("message", "AI 推荐失败")
                )
            }

            val recipeArray = response.optJSONArray("recipes")
                ?: JSONArray()

            val result = mutableListOf<LocalRecipe>()

            for (i in 0 until recipeArray.length()) {
                val item = recipeArray.getJSONObject(i)

                val ingredientJson =
                    item.optJSONArray("ingredients") ?: JSONArray()

                val recipeIngredients =
                    mutableListOf<LocalRecipeIngredient>()

                for (j in 0 until ingredientJson.length()) {
                    val ingredientJsonItem =
                        ingredientJson.getJSONObject(j)

                    recipeIngredients.add(
                        LocalRecipeIngredient(
                            name = ingredientJsonItem.optString("name"),
                            amount = ingredientJsonItem.optString("amount")
                        )
                    )
                }

                val stepsJson = item.optJSONArray("steps") ?: JSONArray()
                val steps = mutableListOf<String>()

                for (j in 0 until stepsJson.length()) {
                    steps.add(stepsJson.optString(j))
                }

                val tagsJson = item.optJSONArray("tags") ?: JSONArray()
                val tags = mutableListOf<String>()

                for (j in 0 until tagsJson.length()) {
                    tags.add(tagsJson.optString(j))
                }

                result.add(
                    LocalRecipe(
                        id = item.optInt("id", i + 10000),
                        name = item.optString("name", "未命名菜谱"),
                        category = item.optString("category", "AI 推荐"),
                        description = item.optString("description", ""),
                        ingredients = recipeIngredients,
                        steps = steps,
                        cookingTime = item.optInt("cookingTime", 20),
                        difficulty = item.optString("difficulty", "简单"),
                        servings = item.optInt("servings", 2),
                        tags = tags
                    )
                )
            }

            result

        } finally {
            connection.disconnect()
        }
    }
}

