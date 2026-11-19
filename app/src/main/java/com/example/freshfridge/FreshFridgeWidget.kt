package com.example.freshfridge

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.freshfridge.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class FreshFridgeWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        CoroutineScope(Dispatchers.IO).launch {

            try {
                // 读取 Room 数据库
                val dao = AppDatabase
                    .getDatabase(context)
                    .ingredientDao()

                val ingredients = dao.getAllIngredientsOnce()

                val today = LocalDate.now()

                // 统计食材数量
                val totalCount = ingredients.size

                val expiringIngredients = ingredients.filter {
                    try {
                        val expiryDate = LocalDate.parse(it.expiryDate)
                        val days = ChronoUnit.DAYS.between(
                            today,
                            expiryDate
                        )
                        days in 0..3
                    } catch (e: Exception) {
                        false
                    }
                }

                val expiredIngredients = ingredients.filter {
                    try {
                        val expiryDate = LocalDate.parse(it.expiryDate)
                        expiryDate.isBefore(today)
                    } catch (e: Exception) {
                        false
                    }
                }

                // 显示临期食材名称
                val names = if (expiringIngredients.isEmpty()) {
                    "暂无临期食材"
                } else {
                    "临期食材：" +
                            expiringIngredients
                                .take(3)
                                .joinToString("、") { it.name }
                }

                // 更新所有桌面小组件
                appWidgetIds.forEach { widgetId ->

                    val views = RemoteViews(
                        context.packageName,
                        R.layout.widget_fresh_fridge
                    )

                    views.setTextViewText(
                        R.id.widget_total,
                        "食材总数：$totalCount"
                    )

                    views.setTextViewText(
                        R.id.widget_expiring,
                        "即将过期：${expiringIngredients.size}"
                    )

                    views.setTextViewText(
                        R.id.widget_expired,
                        "已经过期：${expiredIngredients.size}"
                    )

                    views.setTextViewText(
                        R.id.widget_ingredient_names,
                        names
                    )

                    // 点击小组件打开 App
                    val intent = Intent(
                        context,
                        MainActivity::class.java
                    )

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        widgetId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or
                                PendingIntent.FLAG_IMMUTABLE
                    )

                    views.setOnClickPendingIntent(
                        R.id.widget_root,
                        pendingIntent
                    )

                    appWidgetManager.updateAppWidget(
                        widgetId,
                        views
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}