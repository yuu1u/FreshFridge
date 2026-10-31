package com.example.freshfridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                FreshFridgeHome()
            }
        }
    }
}

data class Ingredient(
    val name: String,
    val amount: String,
    val expire: String
)

@Composable
fun FreshFridgeHome() {

    val ingredients = listOf(
        Ingredient("牛奶", "1 瓶", "今天过期"),
        Ingredient("鸡蛋", "6 个", "还剩 3 天"),
        Ingredient("西红柿", "3 个", "还剩 5 天"),
        Ingredient("西兰花", "1 颗", "还剩 2 天"),
        Ingredient("苹果", "4 个", "还剩 7 天")
    )

    Scaffold(
        containerColor = Color(0xFFF5F8F3),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🏠") },
                    label = { Text("首页") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("🥬") },
                    label = { Text("食材") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("🍳") },
                    label = { Text("菜谱") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("👤") },
                    label = { Text("我的") }
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            item {
                Text(
                    text = "鲜冰箱 FreshFridge",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "让每一份食材都物尽其用",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "今日库存",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = "食材总数",
                        number = "15",
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "即将过期",
                        number = "3",
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "已过期",
                        number = "1",
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "临期提醒",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Text(
                        text = "🥛 牛奶今天过期，记得优先使用！",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF8D5A00),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "我的食材",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(ingredients) { ingredient ->
                IngredientItem(ingredient)
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    number: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = title,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun IngredientItem(ingredient: Ingredient) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = ingredient.amount,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Text(
                text = ingredient.expire,
                color = if (ingredient.expire == "今天过期") {
                    Color(0xFFD32F2F)
                } else {
                    Color(0xFF558B2F)
                },
                fontSize = 13.sp
            )
        }
    }
}