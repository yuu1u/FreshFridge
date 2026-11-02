package com.example.freshfridge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.freshfridge.viewmodel.IngredientViewModel
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
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

private fun getDaysUntilExpiry(expiryDate: String): Long? {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val expiry = format.parse(expiryDate) ?: return null

        // 获取今天的年月日
        val localToday = Calendar.getInstance()

        // 使用 UTC 计算日期差，避免夏令时影响
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        today.clear()
        today.set(
            localToday.get(Calendar.YEAR),
            localToday.get(Calendar.MONTH),
            localToday.get(Calendar.DAY_OF_MONTH)
        )

        val difference = expiry.time - today.timeInMillis

        difference / (24 * 60 * 60 * 1000)
    } catch (e: Exception) {
        null
    }
}
@Composable
fun FreshFridgeHome() {
    val ingredientViewModel: IngredientViewModel = viewModel()

    val ingredients by ingredientViewModel.allIngredients
        .collectAsStateWithLifecycle()

// 首页动态统计
    val totalCount = ingredients.size
// 即将过期：今天到未来3天内过期
    val expiringCount = ingredients.count { ingredient ->
        val days = getDaysUntilExpiry(ingredient.expiryDate)
        days != null && days in 0..3
    }

// 已过期：过期日期早于今天
    val expiredCount = ingredients.count { ingredient ->
        val days = getDaysUntilExpiry(ingredient.expiryDate)
        days != null && days < 0
    }
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
                        number = totalCount.toString(),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "即将过期",
                        number = expiringCount.toString(),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "已过期",
                        number = expiringCount.toString(),
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
                val days = getDaysUntilExpiry(ingredient.expiryDate)

                val expiryText = when {
                    days == null -> "日期无效"
                    days < 0 -> "已过期 ${-days} 天"
                    days == 0L -> "今天过期"
                    else -> "还剩 $days 天"
                }
                Text(
                    text = ingredient.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${ingredient.amount} ${ingredient.unit}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )


                Text(
                    text = expiryText,
                    color = Color(0xFF558B2F),
                    fontSize = 13.sp

                )

            }
        }

    }
}