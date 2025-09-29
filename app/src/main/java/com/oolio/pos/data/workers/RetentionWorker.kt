package com.oolio.pos.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oolio.pos.data.db.POSDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RetentionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val db: POSDatabase
) : CoroutineWorker(appContext,workerParams){

    override suspend fun doWork(): Result {
        return try {
            val totalDaysToDelete = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7
            val failedPrintJobsToDelete = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 30
            db.changeRecordDao().deleteOldSuccess(totalDaysToDelete)
            db.printJobDao().deleteOldCompleted(totalDaysToDelete)
            db.printJobDao().deleteOldFailed(failedPrintJobsToDelete)
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }
}