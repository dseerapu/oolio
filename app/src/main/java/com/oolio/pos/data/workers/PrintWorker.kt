package com.oolio.pos.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oolio.pos.data.print.PrintQueueJobManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PrintWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val printQueueJobManager: PrintQueueJobManager
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val jobId = inputData.getString("PRINT_JOB_ID")
            if (jobId != null) {
                printQueueJobManager.processJobById(jobId)
            } else {
                printQueueJobManager.processPendingJobs()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}