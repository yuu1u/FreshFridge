package com.example.freshfridge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshfridge.CookingRecord
import com.example.freshfridge.Ingredient
import com.example.freshfridge.WidgetUpdateHelper
import com.example.freshfridge.data.AppDatabase
import com.example.freshfridge.data.CookingRecordRepository
import com.example.freshfridge.data.IngredientRepository
import com.example.freshfridge.data.LocalRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    // 食材数据库仓库
    private val repository = IngredientRepository(
        AppDatabase.getDatabase(application).ingredientDao()
    )

    // 食材数量
    private val _ingredientCount = MutableStateFlow(0)
    val ingredientCount: StateFlow<Int> = _ingredientCount

    // 所有食材列表
    val allIngredients: StateFlow<List<Ingredient>> =
        repository.allIngredients.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 烹饪记录数据库仓库
    private val cookingRecordRepository = CookingRecordRepository(
        AppDatabase.getDatabase(application).cookingRecordDao()
    )

    // 所有烹饪记录
    val allCookingRecords: StateFlow<List<CookingRecord>> =
        cookingRecordRepository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 烹饪次数
    private val _cookingCount = MutableStateFlow(0)
    val cookingCount: StateFlow<Int> = _cookingCount

    // 初始化
    init {
        viewModelScope.launch {

            // 如果数据库为空，添加初始食材
            if (repository.getIngredientCount() == 0) {

                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

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

            // 更新食材数量
            _ingredientCount.value = repository.getIngredientCount()

            // 更新烹饪记录数量
            _cookingCount.value = cookingRecordRepository.getRecordCount()

            // 初始化完成后刷新桌面小组件
            WidgetUpdateHelper.update(getApplication())
        }
    }

    // 添加食材
    fun insertIngredient(ingredient: Ingredient) {
        viewModelScope.launch {

            repository.insertIngredient(ingredient)

            _ingredientCount.value = repository.getIngredientCount()

            // 添加完成后刷新桌面小组件
            WidgetUpdateHelper.update(getApplication())
        }
    }

    // 修改食材
    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {

            repository.updateIngredient(ingredient)

            _ingredientCount.value = repository.getIngredientCount()

            // 修改完成后刷新桌面小组件
            WidgetUpdateHelper.update(getApplication())
        }
    }

    // 删除食材
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {

            repository.deleteIngredient(ingredient)

            _ingredientCount.value = repository.getIngredientCount()

            // 删除完成后刷新桌面小组件
            WidgetUpdateHelper.update(getApplication())
        }
    }

    // 添加烹饪记录
    fun insertCookingRecord(record: CookingRecord) {
        viewModelScope.launch {

            cookingRecordRepository.insertRecord(record)

            _cookingCount.value = cookingRecordRepository.getRecordCount()
        }
    }

    // 删除烹饪记录
    fun deleteCookingRecord(recordId: Int) {
        viewModelScope.launch {

            cookingRecordRepository.deleteRecord(recordId)

            _cookingCount.value = cookingRecordRepository.getRecordCount()
        }
    }

    // AI 菜谱数据
    private val _aiRecipes = MutableStateFlow<List<LocalRecipe>>(emptyList())
    val aiRecipes: StateFlow<List<LocalRecipe>> = _aiRecipes

    // 更新 AI 菜谱
    fun updateAiRecipes(recipes: List<LocalRecipe>) {
        _aiRecipes.value = recipes
    }
}