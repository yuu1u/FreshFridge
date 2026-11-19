package com.example.freshfridge.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ExpiryWorkScheduler {

    private const val WORK_NAME = "fresh_fridge_expiry_check"

    // 安排过期检查任务
    fun schedule(context: Context) {

        val workManager = WorkManager.getInstance(context)

        // 允许设备没有网络时执行
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // 立即安排一次检查
        val immediateWork =
            OneTimeWorkRequestBuilder<ExpiryCheckWorker>()
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            "fresh_fridge_expiry_first_check",
            ExistingWorkPolicy.KEEP,
            immediateWork
        )

        // 每24小时自动检查一次
        val periodicWork =
            PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
                24,
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }
}

