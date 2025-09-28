package com.oolio.pos

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.oolio.pos.data.workers.FetchSyncWorker
import com.oolio.pos.data.workers.PrintWorker
import com.oolio.pos.data.workers.PushSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class PostApp: Application() {

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
}
