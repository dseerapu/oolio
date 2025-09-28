package com.oolio.pos.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oolio.pos.data.datastore.LastSyncStore
import com.oolio.pos.domain.ResolveConflictUseCase
import com.oolio.pos.domain.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val lastSyncStore: LastSyncStore,
    private val conflictUseCase: ResolveConflictUseCase
): CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        return try {
            val lastSync = lastSyncStore.getLastSync()
            val remoteOrders = syncRepository.fetchRemoteOrders(lastSync)
            val remoteInventory = syncRepository.fetchRemoteInventory(lastSync)
            remoteOrders.forEach { remoteOrder->
                val localOrder = syncRepository.fetchOrderById(remoteOrder.id)
                val resolved = localOrder?.let {
                    conflictUseCase.resolveOrder(it, remoteOrder)
                }?:remoteOrder
                syncRepository.upsertOrder(resolved)
            }

            remoteInventory.forEach { remoteInventory->
                val localInventory = syncRepository.fetchInventoryById(remoteInventory.inventoryId)
                val resolved = localInventory?.let {
                    conflictUseCase.resolveInventory(it, remoteInventory)
                }?:remoteInventory

                syncRepository.upsertInventory(resolved)
            }

            lastSyncStore.setLastSync(System.currentTimeMillis())
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }


}