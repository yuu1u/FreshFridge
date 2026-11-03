package com.example.freshfridge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooking_records")
data class CookingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // 菜谱名称
    val recipeName: String,

    // 烹饪时间（分钟）
    val cookingTime: Int,

    // 完成烹饪的时间戳
    val cookedAt: Long = System.currentTimeMillis()
)