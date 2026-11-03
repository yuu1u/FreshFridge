package com.example.freshfridge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.freshfridge.Ingredient
import com.example.freshfridge.Recipe
import com.example.freshfridge.data.RecipeData
import com.example.freshfridge.viewmodel.IngredientViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun RecipeScreen(
    viewModel: IngredientViewModel
) {
    val ingredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    // 根据库存匹配菜谱
    val recommendations = remember(ingredients) {
        getRecommendedRecipes(ingredients)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "智能菜谱推荐",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "根据冰箱现有食材，为你推荐合适的菜谱",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (ingredients.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("冰箱里还没有食材，请先添加食材")
            }
        } else if (recommendations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("暂时没有匹配的菜谱，可以先添加更多食材")
            }
        } else {
            Text(
                text = "为你找到 ${recommendations.size} 道推荐菜谱",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = recommendations,
                    key = { it.recipe.id }
                ) { recommendation ->

                    RecipeCard(
                        recommendation = recommendation
                    )
                }
            }
        }
    }
}

private data class RecipeRecommendation(
    val recipe: Recipe,
    val matchedIngredients: List<String>,
    val missingIngredients: List<String>,
    val expiringCount: Int,
    val matchRate: Int
)

// 推荐算法
private fun getRecommendedRecipes(
    ingredients: List<Ingredient>
): List<RecipeRecommendation> {

    val inventoryNames = ingredients.map {
        it.name.trim()
    }.filter {
        it.isNotEmpty()
    }.toSet()

    return RecipeData.recipes.mapNotNull { recipe ->

        val matched = recipe.ingredients.filter { required ->
            inventoryNames.any { available ->
                available.contains(required) ||
                        required.contains(available)
            }
        }

        val missing = recipe.ingredients.filter { required ->
            required !in matched
        }

        // 至少匹配一种食材才推荐
        if (matched.isEmpty()) {
            null
        } else {

            val expiringCount = ingredients.count { ingredient ->
                matched.any { matchedName ->
                    ingredient.name.trim().contains(matchedName) ||
                            matchedName.contains(ingredient.name.trim())
                } && isExpiringSoon(ingredient.expiryDate)
            }

            val matchRate = (
                    matched.size * 100 / recipe.ingredients.size
                    )

            RecipeRecommendation(
                recipe = recipe,
                matchedIngredients = matched,
                missingIngredients = missing,
                expiringCount = expiringCount,
                matchRate = matchRate
            )
        }
    }.sortedWith(
        compareByDescending<RecipeRecommendation> {
            it.expiringCount
        }.thenByDescending {
            it.matchRate
        }.thenBy {
            it.recipe.cookingTime
        }
    )
}

// 判断食材是否在三天内过期，包含今天
private fun isExpiringSoon(expiryDate: String): Boolean {
    val days = try {
        ChronoUnit.DAYS.between(
            LocalDate.now(),
            LocalDate.parse(expiryDate)
        )
    } catch (e: Exception) {
        Regex("""还剩\s*(\d+)\s*天""")
            .find(expiryDate)
            ?.groupValues
            ?.get(1)
            ?.toLongOrNull()
    }

    return days != null && days in 0..3
}

// 菜谱卡片
@Composable
private fun RecipeCard(
    recommendation: RecipeRecommendation
) {
    val recipe = recommendation.recipe

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = recipe.description,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("匹配程度：${recommendation.matchRate}%")

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "已有食材：${recommendation.matchedIngredients.joinToString("、")}"
            )

            if (recommendation.missingIngredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "还缺少：${recommendation.missingIngredients.joinToString("、")}",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "食材齐全，可以开始制作！",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (recommendation.expiringCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "优先推荐：包含 ${recommendation.expiringCount} 种临期食材",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("烹饪时间：${recipe.cookingTime} 分钟")

            Text("难度：${recipe.difficulty}")

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "所需食材",
                style = MaterialTheme.typography.titleSmall
            )

            recipe.ingredients.forEach { ingredient ->
                Text("• $ingredient")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "制作步骤",
                style = MaterialTheme.typography.titleSmall
            )

            recipe.steps.forEachIndexed { index, step ->
                Text("${index + 1}. $step")
            }
        }
    }
}
