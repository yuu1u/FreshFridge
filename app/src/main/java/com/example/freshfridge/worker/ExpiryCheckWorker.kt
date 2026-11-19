package com.example.freshfridge.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.freshfridge.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ExpiryCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {

            withContext(Dispatchers.IO) {

                // 获取 Room 数据库
                val database = AppDatabase.getDatabase(
                    applicationContext
                )

                // 获取全部食材
                val ingredients = database
                    .ingredientDao()
                    .getAllIngredientsOnce()

                // 创建通知渠道
                NotificationHelper.createNotificationChannel(
                    applicationContext
                )

                // 日期格式
                val format = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).apply {
                    isLenient = false
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                // 获取今天的日期
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

                // 遍历所有食材
                ingredients.forEach { ingredient ->

                    val expiryDate = try {
                        format.parse(ingredient.expiryDate)
                    } catch (e: Exception) {
                        null
                    } ?: return@forEach

                    val days = (
                            (expiryDate.time - today.timeInMillis) /
                                    (24 * 60 * 60 * 1000)
                            ).toInt()

                    when {

                        // 已经过期
                        days < 0 -> {

                            NotificationHelper.showExpiryNotification(
                                context = applicationContext,
                                ingredientId = ingredient.id,
                                title = "食材已经过期！",
                                message = "${ingredient.name} 已经过期 ${-days} 天，请及时处理。",
                                isExpired = true
                            )
                        }

                        // 三天内即将过期，包含今天
                        days in 0..3 -> {

                            val message = if (days == 0) {
                                "${ingredient.name} 今天就要过期，请尽快使用！"
                            } else {
                                "${ingredient.name} 还有 $days 天过期，请优先使用。"
                            }

                            NotificationHelper.showExpiryNotification(
                                context = applicationContext,
                                ingredientId = ingredient.id,
                                title = "食材即将过期",
                                message = message,
                                isExpired = false
                            )
                        }
                    }
                }
            }

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            Result.retry()
        }
    }
}

