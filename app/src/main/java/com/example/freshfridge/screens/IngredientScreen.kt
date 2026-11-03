package com.example.freshfridge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.freshfridge.Ingredient
import com.example.freshfridge.viewmodel.IngredientViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun IngredientScreen(
    viewModel: IngredientViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Ingredient) -> Unit
) {
    val ingredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的食材",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(onClick = onAddClick) {
                Text("添加食材")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (ingredients.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("还没有食材，点击右上角添加")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = ingredients,
                    key = { it.id }
                ) { ingredient ->

                    IngredientCard(
                        ingredient = ingredient,
                        onEditClick = {
                            onEditClick(ingredient)
                        },
                        onDeleteClick = {
                            viewModel.deleteIngredient(ingredient)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: Ingredient,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val days = calculateDays(ingredient.expiryDate)

    val expiryText = when {
        days == null -> "日期无效"
        days < 0 -> "已过期 ${-days} 天"
        days == 0L -> "今天过期"
        else -> "还剩 $days 天"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("${ingredient.amount} ${ingredient.unit}")

                Text("过期时间：${ingredient.expiryDate}")

                Text(
                    text = expiryText,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Column {
                TextButton(onClick = onEditClick) {
                    Text("编辑")
                }

                TextButton(onClick = onDeleteClick) {
                    Text(
                        text = "删除",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun calculateDays(expiryDate: String): Long? {
    return try {
        val date = LocalDate.parse(expiryDate)
        ChronoUnit.DAYS.between(LocalDate.now(), date)
    } catch (e: Exception) {
        Regex("""还剩\s*(\d+)\s*天""")
            .find(expiryDate)
            ?.groupValues
            ?.get(1)
            ?.toLongOrNull()
    }
}