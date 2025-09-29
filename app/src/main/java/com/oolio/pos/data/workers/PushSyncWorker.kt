package com.oolio.pos.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oolio.pos.domain.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PushSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
           val pendingChanges =  syncRepository.getPendingChanges()
            pendingChanges.chunked(20).forEach { batch ->
                batch.forEach { change ->
                    try {
                        syncRepository.pushChange(change)
                        syncRepository.markChangeSuccess(change)
                    } catch (e: Exception) {
                        syncRepository.markChangeFailed(change)
                    }
                }
            }

            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}