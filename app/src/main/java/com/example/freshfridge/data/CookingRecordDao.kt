package com.example.freshfridge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.freshfridge.CookingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CookingRecordDao {

    // 获取全部烹饪记录，按时间倒序排列
    @Query("SELECT * FROM cooking_records ORDER BY cookedAt DESC")
    fun getAllRecords(): Flow<List<CookingRecord>>

    // 添加烹饪记录
    @Insert
    suspend fun insertRecord(record: CookingRecord)

    // 获取烹饪总次数
    @Query("SELECT COUNT(*) FROM cooking_records")
    suspend fun getRecordCount(): Int

    // 删除一条烹饪记录
    @Query("DELETE FROM cooking_records WHERE id = :recordId")
    suspend fun deleteRecord(recordId: Int)
}