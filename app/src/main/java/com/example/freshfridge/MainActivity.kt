package com.example.freshfridge
import com.example.freshfridge.screens.RecipeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.freshfridge.screens.AddingIngredientScreen
import com.example.freshfridge.screens.EditIngredientScreen
import com.example.freshfridge.screens.IngredientScreen
import com.example.freshfridge.viewmodel.IngredientViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                FreshFridgeApp()
            }
        }
    }
}

// 计算距离过期还有多少天
private fun getDaysUntilExpiry(expiryDate: String): Long? {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val expiry = format.parse(expiryDate) ?: return null

        val localToday = Calendar.getInstance()

        val today = Calendar.getInstance(
            TimeZone.getTimeZone("UTC")
        )

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

// App 主界面与页面导航
@Composable
fun FreshFridgeApp() {

    val ingredientViewModel: IngredientViewModel = viewModel()

    var currentScreen by rememberSaveable {
        mutableStateOf("home")
    }

    // 当前选中的食材，用于编辑页面
    var selectedIngredient by remember {
        mutableStateOf<Ingredient?>(null)
    }

    Scaffold(
        containerColor = Color(0xFFF5F8F3),

        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = {
                        currentScreen = "home"
                    },
                    icon = { Text("🏠") },
                    label = { Text("首页") }
                )

                NavigationBarItem(
                    selected = currentScreen == "ingredients",
                    onClick = {
                        currentScreen = "ingredients"
                    },
                    icon = { Text("🥬") },
                    label = { Text("食材") }
                )

                NavigationBarItem(
                    selected = currentScreen == "recipes",
                    onClick = {
                        currentScreen = "recipes"
                    },
                    icon = { Text("🍳") },
                    label = { Text("菜谱") }
                )

                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = {
                        currentScreen = "profile"
                    },
                    icon = { Text("👤") },
                    label = { Text("我的") }
                )
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (currentScreen) {

                // 首页
                "home" -> {
                    FreshFridgeHome(
                        viewModel = ingredientViewModel
                    )
                }

                // 食材管理页面
                "ingredients" -> {
                    IngredientScreen(
                        viewModel = ingredientViewModel,

                        onAddClick = {
                            currentScreen = "add"
                        },

                        onEditClick = { ingredient ->
                            selectedIngredient = ingredient
                            currentScreen = "edit"
                        }
                    )
                }

                // 添加食材页面
                "add" -> {
                    AddingIngredientScreen(
                        viewModel = ingredientViewModel,
                        onBack = {
                            currentScreen = "ingredients"
                        }
                    )
                }

                // 编辑食材页面
                "edit" -> {
                    selectedIngredient?.let { ingredient ->

                        EditIngredientScreen(
                            ingredient = ingredient,
                            viewModel = ingredientViewModel,

                            onBack = {
                                currentScreen = "ingredients"
                                selectedIngredient = null
                            }
                        )
                    }
                }

                // 智能菜谱推荐页面
                "recipes" -> {
                    RecipeScreen(
                        viewModel = ingredientViewModel
                    )
                }

                // 我的页面（后续开发）
                "profile" -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("个人中心正在开发中")
                    }
                }
            }
        }
    }
}

// 首页
@Composable
fun FreshFridgeHome(
    viewModel: IngredientViewModel
) {

    val ingredients by viewModel.allIngredients
        .collectAsStateWithLifecycle()

    // 食材总数
    val totalCount = ingredients.size

    // 即将过期：未来 3 天内过期，包含今天
    val expiringCount = ingredients.count { ingredient ->
        val days = getDaysUntilExpiry(ingredient.expiryDate)
        days != null && days in 0..3
    }

    // 已经过期
    val expiredCount = ingredients.count { ingredient ->
        val days = getDaysUntilExpiry(ingredient.expiryDate)
        days != null && days < 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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

        // 动态统计卡片
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
                    number = expiredCount.toString(),
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

            if (expiringCount > 0) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Text(
                        text = "⚠️ 当前有 $expiringCount 种食材即将过期，请优先使用！",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF8D5A00),
                        fontSize = 14.sp
                    )
                }

            } else {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Text(
                        text = "🎉 暂时没有即将过期的食材",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "我的食材",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 数据库中的食材列表
        if (ingredients.isEmpty()) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "还没有食材，点击底部「食材」添加吧",
                        color = Color.Gray
                    )
                }
            }

        } else {

            items(
                items = ingredients,
                key = { ingredient -> ingredient.id }
            ) { ingredient ->

                IngredientItem(ingredient)
            }
        }
    }
}

// 首页统计卡片
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

// 首页食材卡片
@Composable
fun IngredientItem(
    ingredient: Ingredient
) {

    val days = getDaysUntilExpiry(ingredient.expiryDate)

    val expiryText = when {
        days == null -> "日期无效"
        days < 0 -> "已过期 ${-days} 天"
        days == 0L -> "今天过期"
        else -> "还剩 $days 天"
    }

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
                    text = "${ingredient.amount} ${ingredient.unit}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Text(
                    text = expiryText,
                    color = if (days != null && days < 0) {
                        Color.Red
                    } else {
                        Color(0xFF558B2F)
                    },
                    fontSize = 13.sp
                )
            }
        }
    }
}