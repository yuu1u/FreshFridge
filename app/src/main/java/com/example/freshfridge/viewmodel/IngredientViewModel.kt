package com.example.freshfridge.viewmodel
import kotlinx.coroutines.flow.MutableStateFlow
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshfridge.Ingredient
import com.example.freshfridge.data.AppDatabase
import com.example.freshfridge.data.IngredientRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IngredientRepository(
        AppDatabase.getDatabase(application).ingredientDao()
    )
    // 数据库中的食材总数
    private val _ingredientCount = MutableStateFlow(0)
    val ingredientCount: StateFlow<Int> = _ingredientCount
    // 初始化时读取数据库中的食材总数
    init {
        viewModelScope.launch {
            // 数据库为空时，自动添加初始食材
            if (repository.getIngredientCount() == 0) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                fun getExpiryDate(days: Int): String {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, days)
                    return dateFormat.format(calendar.time)
                }

                val initialIngredients = listOf(
                    Ingredient(
                        name = "牛奶",
                        amount = "1",
                        unit = "瓶",
                        expiryDate = getExpiryDate(3)
                    ),
                    Ingredient(
                        name = "鸡蛋",
                        amount = "6",
                        unit = "个",
                        expiryDate = getExpiryDate(5)
                    ),
                    Ingredient(
                        name = "西红柿",
                        amount = "3",
                        unit = "个",
                        expiryDate = getExpiryDate(2)
                    ),
                    Ingredient(
                        name = "西兰花",
                        amount = "1",
                        unit = "颗",
                        expiryDate = getExpiryDate(4)
                    ),
                    Ingredient(
                        name = "苹果",
                        amount = "4",
                        unit = "个",
                        expiryDate = getExpiryDate(7)
                    )
                )

                initialIngredients.forEach {
                    repository.insertIngredient(it)
                }
            }

            // 更新食材总数
            _ingredientCount.value = repository.getIngredientCount()
        }
    }
    // 观察数据库中的全部食材
    val allIngredients: StateFlow<List<Ingredient>> =
        repository.allIngredients.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )




    // 添加食材
    fun insertIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.insertIngredient(ingredient)
            _ingredientCount.value = repository.getIngredientCount()
        }
    }

    // 修改食材
    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.updateIngredient(ingredient)
            _ingredientCount.value = repository.getIngredientCount()
        }
    }

    // 删除食材
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
            _ingredientCount.value = repository.getIngredientCount()
        }
    }
}