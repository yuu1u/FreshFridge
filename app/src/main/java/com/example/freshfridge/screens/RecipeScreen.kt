package com.example.freshfridge.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.freshfridge.Recipe
import com.example.freshfridge.data.AiRecipeApi
import com.example.freshfridge.data.LocalRecipe
import com.example.freshfridge.data.RecipeRepository
import com.example.freshfridge.data.RecipeRecommendationEngine
import com.example.freshfridge.viewmodel.IngredientViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RecipeScreen(
    viewModel: IngredientViewModel,
    onRecipeClick: (Recipe) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val ingredients by viewModel.allIngredients.collectAsStateWithLifecycle()
    val aiRecipes by viewModel.aiRecipes.collectAsStateWithLifecycle()

    val repository = remember(context) {
        RecipeRepository(context)
    }

    val recommendationEngine = remember {
        RecipeRecommendationEngine()
    }

    var recipes by remember {
        mutableStateOf(emptyList<LocalRecipe>())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isAiLoading by remember {
        mutableStateOf(false)
    }

    var aiError by remember {
        mutableStateOf("")
    }

    // 读取本地菜谱
    LaunchedEffect(repository) {
        recipes = withContext(Dispatchers.IO) {
            repository.loadRecipes()
        }
        isLoading = false
    }

    // 本地菜谱智能匹配
    val recommendations = remember(recipes, ingredients) {
        recommendationEngine.recommendRecipes(
            recipes = recipes,
            inventory = ingredients
        )
    }

    // 将 AI 菜谱转换为原有 Recipe 类型
    fun openRecipe(recipe: LocalRecipe) {
        val oldRecipe = Recipe(
            id = recipe.id,
            name = recipe.name,
            description = recipe.description,
            ingredients = recipe.ingredients.map {
                "${it.name}（${it.amount}）"
            },
            steps = recipe.steps,
            cookingTime = recipe.cookingTime,
            difficulty = recipe.difficulty
        )

        onRecipeClick(oldRecipe)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "智能菜谱推荐",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "根据冰箱现有食材，为你推荐合适的菜谱",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 库存统计
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "当前库存：${ingredients.size} 种食材",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "本地菜谱库：${recipes.size} 道菜",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI 推荐按钮
        Button(
            onClick = {
                scope.launch {
                    isAiLoading = true
                    aiError = ""

                    try {
                        val result = AiRecipeApi.recommendRecipes(
                            ingredients = ingredients,
                            count = 5
                        )

                        if (result.isNotEmpty()) {
                            viewModel.updateAiRecipes(result)
                        } else {
                            aiError = "AI 暂时没有返回菜谱，请稍后重试。"
                        }

                    } catch (e: Exception) {
                        aiError = "AI 推荐失败：${e.message ?: "请检查网络和后端服务"}"
                    } finally {
                        isAiLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = ingredients.isNotEmpty() && !isAiLoading
        ) {
            if (isAiLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("DeepSeek 正在生成菜谱...")
            } else {
                Text("✨ AI 智能推荐菜谱")
            }
        }

        if (ingredients.isEmpty()) {
            Text(
                text = "请先添加冰箱食材，再使用 AI 推荐。",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // AI 错误提示
        if (aiError.isNotEmpty()) {
            Text(
                text = aiError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // AI 推荐结果
        if (aiRecipes.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "DeepSeek AI 为你推荐（${aiRecipes.size}道）",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = aiRecipes,
                    key = { "ai_${it.id}" }
                ) { recipe ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openRecipe(recipe)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(recipe.description)

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("分类：${recipe.category}")
                            Text("烹饪时间：${recipe.cookingTime}分钟")
                            Text("难度：${recipe.difficulty}")

                            Text(
                                text = "所需食材：${
                                    recipe.ingredients.joinToString("、") {
                                        "${it.name} ${it.amount}"
                                    }
                                }"
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "点击查看菜谱详情",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "本地菜谱推荐",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            recipes.isEmpty() -> {
                Text(
                    text = "暂时没有读取到菜谱，请检查 recipes.json 文件。",
                    color = MaterialTheme.colorScheme.error
                )
            }

            ingredients.isEmpty() -> {
                Text("冰箱里还没有食材，先去添加一些食材吧！")
            }

            recommendations.isEmpty() -> {
                Text("暂时没有找到匹配的本地菜谱，可以尝试添加更多食材。")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = recommendations,
                        key = { "local_${it.recipe.id}" }
                    ) { recommendation ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openRecipe(recommendation.recipe)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = recommendation.recipe.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(recommendation.recipe.description)

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("分类：${recommendation.recipe.category}")
                                Text("匹配度：${recommendation.matchPercentage}%")
                                Text("烹饪时间：${recommendation.recipe.cookingTime}分钟")
                                Text("难度：${recommendation.recipe.difficulty}")

                                Text(
                                    text = "已有食材：${
                                        recommendation.matchedIngredients.joinToString("、")
                                    }"
                                )

                                if (recommendation.missingIngredients.isNotEmpty()) {
                                    Text(
                                        text = "还缺少：${
                                            recommendation.missingIngredients.joinToString("、")
                                        }",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Text(
                                        text = "食材齐全，可以开始烹饪！",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (recommendation.expiringIngredientCount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "临期食材：${recommendation.expiringIngredientCount}种，建议优先使用",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "点击查看菜谱详情",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}