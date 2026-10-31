package com.example.freshfridge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.freshfridge.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    // 查询全部食材
    @Query("SELECT * FROM ingredients ORDER BY id DESC")
    fun getAllIngredients(): Flow<List<Ingredient>>
    // 查询食材总数
    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun getIngredientCount(): Int
    // 添加食材
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    // 修改食材
    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    // 删除食材
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    // 根据 ID 查询食材
    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getIngredientById(id: Int): Ingredient?
}