package com.example.freshfridge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.freshfridge.Ingredient
import com.example.freshfridge.viewmodel.IngredientViewModel

@Composable
fun EditIngredientScreen(
    ingredient: Ingredient,
    viewModel: IngredientViewModel,
    onBack: () -> Unit
) {
    var name by remember(ingredient.id) {
        mutableStateOf(ingredient.name)
    }

    var amount by remember(ingredient.id) {
        mutableStateOf(ingredient.amount)
    }

    var unit by remember(ingredient.id) {
        mutableStateOf(ingredient.unit)
    }

    var expiryDate by remember(ingredient.id) {
        mutableStateOf(ingredient.expiryDate)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "编辑食材",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("食材名称") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("数量") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("单位，例如：个、瓶、斤") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = expiryDate,
            onValueChange = { expiryDate = it },
            label = { Text("过期日期（yyyy-MM-dd）") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "日期格式示例：2026-11-10",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (
                    name.isBlank() ||
                    amount.isBlank() ||
                    unit.isBlank() ||
                    expiryDate.isBlank()
                ) {
                    errorMessage = "请填写完整的食材信息"
                } else {
                    try {
                        java.time.LocalDate.parse(expiryDate)

                        val updatedIngredient = ingredient.copy(
                            name = name.trim(),
                            amount = amount.trim(),
                            unit = unit.trim(),
                            expiryDate = expiryDate.trim()
                        )

                        viewModel.updateIngredient(updatedIngredient)
                        onBack()

                    } catch (e: Exception) {
                        errorMessage = "日期格式不正确，请使用 yyyy-MM-dd"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存修改")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("取消")
        }
    }
}

