package com.example.freshfridge.data

import com.example.freshfridge.Ingredient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 单道菜谱的推荐结果
 */
data class RecipeRecommendation(
    val recipe: LocalRecipe,
    val matchedIngredients: List<String>,
    val missingIngredients: List<String>,
    val matchPercentage: Int,
    val expiringIngredientCount: Int
)

/**
 * 本地智能菜谱推荐引擎
 */
class RecipeRecommendationEngine {

    /**
     * 根据库存推荐菜谱
     */
    fun recommendRecipes(
        recipes: List<LocalRecipe>,
        inventory: List<Ingredient>
    ): List<RecipeRecommendation> {

        // 当前库存食材名称
        val inventoryNames = inventory.map {
            normalizeName(it.name)
        }.toSet()

        // 计算临期食材
        val expiringNames = inventory
            .filter { isExpiringSoon(it.expiryDate) }
            .map { normalizeName(it.name) }
            .toSet()

        val recommendations = recipes.mapNotNull { recipe ->

            val matched = recipe.ingredients.filter {
                normalizeName(it.name) in inventoryNames
            }

            val missing = recipe.ingredients.filter {
                normalizeName(it.name) !in inventoryNames
            }

            // 没有任何匹配食材的菜谱暂不推荐
            if (matched.isEmpty()) {
                return@mapNotNull null
            }

            val percentage = (
                    matched.size.toDouble() /
                            recipe.ingredients.size * 100
                    ).roundToInt()

            // 计算使用临期食材的数量
            val expiringCount = matched.count {
                normalizeName(it.name) in expiringNames
            }

            RecipeRecommendation(
                recipe = recipe,
                matchedIngredients = matched.map { it.name },
                missingIngredients = missing.map { it.name },
                matchPercentage = percentage,
                expiringIngredientCount = expiringCount
            )
        }

        // 排序：匹配度优先，其次考虑临期食材
        return recommendations.sortedWith(
            compareByDescending<RecipeRecommendation> {
                it.matchPercentage
            }.thenByDescending {
                it.expiringIngredientCount
            }.thenBy {
                it.recipe.cookingTime
            }
        )
    }

    /**
     * 判断食材是否在3天内过期
     */
    private fun isExpiringSoon(expiryDate: String): Boolean {

        return try {

            val formatter = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

            formatter.isLenient = false

            val expiry = formatter.parse(expiryDate)
                ?: return false

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val expiryCalendar = Calendar.getInstance().apply {
                time = expiry
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val days = (
                    expiryCalendar.timeInMillis -
                            today.timeInMillis
                    ) / (24 * 60 * 60 * 1000)

            days in 0..3

        } catch (e: Exception) {
            false
        }
    }

    /**
     * 统一食材名称格式
     */
    private fun normalizeName(name: String): String {
        return name.trim()
            .replace(" ", "")
            .lowercase(Locale.ROOT)
    }
}

