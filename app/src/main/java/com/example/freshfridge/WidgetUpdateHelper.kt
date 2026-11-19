package com.example.freshfridge

import android.appwidget.AppWidgetManager
import android.content.ComponentName

object WidgetUpdateHelper {

    // 主动刷新所有 FreshFridge 桌面小组件
    fun update(context: android.content.Context) {

        val appWidgetManager = AppWidgetManager.getInstance(context)

        // 获取已经添加到桌面的所有小组件
        val componentName = ComponentName(
            context,
            FreshFridgeWidget::class.java
        )

        val widgetIds = appWidgetManager.getAppWidgetIds(
            componentName
        )

        // 有小组件时才执行刷新
        if (widgetIds.isNotEmpty()) {

            FreshFridgeWidget().onUpdate(
                context,
                appWidgetManager,
                widgetIds
            )
        }
    }
}