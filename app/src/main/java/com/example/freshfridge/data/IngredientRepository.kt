package com.example.freshfridge.data

import com.example.freshfridge.Ingredient
import kotlinx.coroutines.flow.Flow

class IngredientRepository(
    private val ingredientDao: IngredientDao
) {

    // 观察全部食材
    val allIngredients: Flow<List<Ingredient>> =
        ingredientDao.getAllIngredients()
    // 查询食材数量
    suspend fun getIngredientCount(): Int {
        return ingredientDao.getIngredientCount()
    }
    // 添加食材
    suspend fun insertIngredient(ingredient: Ingredient) {
        ingredientDao.insertIngredient(ingredient)
    }

    // 修改食材
    suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.updateIngredient(ingredient)
    }

    // 删除食材
    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.deleteIngredient(ingredient)
    }

    // 根据 ID 查询食材
    suspend fun getIngredientById(id: Int): Ingredient? {
        return ingredientDao.getIngredientById(id)
    }
}