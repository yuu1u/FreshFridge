package com.example.freshfridge.screens

import android.app.DatePickerDialog
import android.widget.Toast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.freshfridge.Ingredient
import com.example.freshfridge.viewmodel.IngredientViewModel

import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingIngredientScreen(
    viewModel: IngredientViewModel,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    // 食材名称
    var name by remember {
        mutableStateOf("")
    }

    // 食材数量
    var amount by remember {
        mutableStateOf("")
    }

    // 食材单位
    var unit by remember {
        mutableStateOf("个")
    }

    // 过期日期
    var expiryDate by remember {
        mutableStateOf("")
    }

    // 单位下拉菜单状态
    var expanded by remember {
        mutableStateOf(false)
    }

    // 可选择的单位
    val units = listOf(
        "个",
        "瓶",
        "袋",
        "盒",
        "斤",
        "克",
        "千克"
    )

    // 日期选择器
    val calendar = Calendar.getInstance()

    fun showDatePicker() {

        DatePickerDialog(
            context,
            { _, year, month, day ->

                expiryDate = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    day
                )

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        ).show()
    }

    // 页面布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // 页面标题
        Text(
            text = "添加食材",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // 食材名称输入框
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("食材名称")
            },
            placeholder = {
                Text("请输入食材名称")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // 食材数量输入框
        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
            },
            label = {
                Text("食材数量")
            },
            placeholder = {
                Text("请输入数量")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // 单位选择框
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            OutlinedTextField(
                value = unit,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("数量单位")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                units.forEach { selectedUnit ->

                    DropdownMenuItem(
                        text = {
                            Text(selectedUnit)
                        },
                        onClick = {

                            unit = selectedUnit
                            expanded = false

                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // 过期日期选择框
        OutlinedTextField(
            value = expiryDate,
            onValueChange = {},
            readOnly = true,
            label = {
                Text("过期日期")
            },
            placeholder = {
                Text("请选择过期日期")
            },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {

                TextButton(
                    onClick = {
                        showDatePicker()
                    }
                ) {
                    Text("选择日期")
                }

            }
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // 保存按钮
        Button(
            onClick = {

                // 检查输入内容
                if (
                    name.isBlank() ||
                    amount.isBlank() ||
                    expiryDate.isBlank()
                ) {

                    Toast.makeText(
                        context,
                        "请填写完整的食材信息",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    // 创建食材对象并写入 Room
                    viewModel.insertIngredient(
                        Ingredient(
                            name = name.trim(),
                            amount = amount.trim(),
                            unit = unit,
                            expiryDate = expiryDate
                        )
                    )

                    Toast.makeText(
                        context,
                        "食材添加成功",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 返回食材管理页面
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("保存食材")

        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // 取消按钮
        OutlinedButton(
            onClick = {
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("取消")

        }
    }
}