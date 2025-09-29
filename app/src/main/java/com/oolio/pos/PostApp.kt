package com.oolio.pos

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.oolio.pos.data.print.PrintQueueJobManager
import com.oolio.pos.data.workers.FetchSyncWorker
import com.oolio.pos.data.workers.PrintWorker
import com.oolio.pos.data.workers.PushSyncWorker
import com.oolio.pos.data.workers.RetentionWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class PostApp: Application() {

    @Inject
    lateinit var printQueueJobManager: PrintQueueJobManager

    override fun onCreate() {
        super.onCreate()

        //schedule workers
        scheduleWorkers(this)
    }
}

fun scheduleWorkers(context: Context) {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val pushWork = PeriodicWorkRequestBuilder<PushSyncWorker>(5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "PushSyncWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        pushWork
    )

    val fetchWork = PeriodicWorkRequestBuilder<FetchSyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "FetchSyncWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        fetchWork
    )

    val periodicWork = PeriodicWorkRequestBuilder<PrintWorker>(5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "PrintWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWork
    )

    val retentionWorkRequest = PeriodicWorkRequestBuilder<RetentionWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "retentionWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        retentionWorkRequest
    )
}

fun calculateInitialDelay(): Long {
    val now = Calendar.getInstance()

    val nextRun = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 2)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If time already passed today, schedule for tomorrow
        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    return nextRun.timeInMillis - now.timeInMillis
}

