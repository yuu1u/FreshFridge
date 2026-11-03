package com.example.freshfridge.data

import com.example.freshfridge.CookingRecord
import kotlinx.coroutines.flow.Flow

class CookingRecordRepository(
    private val cookingRecordDao: CookingRecordDao
) {

    // 所有烹饪记录
    val allRecords: Flow<List<CookingRecord>> =
        cookingRecordDao.getAllRecords()

    // 添加记录
    suspend fun insertRecord(record: CookingRecord) {
        cookingRecordDao.insertRecord(record)
    }

    // 获取烹饪总次数
    suspend fun getRecordCount(): Int {
        return cookingRecordDao.getRecordCount()
    }

    // 删除记录
    suspend fun deleteRecord(recordId: Int) {
        cookingRecordDao.deleteRecord(recordId)
    }
}