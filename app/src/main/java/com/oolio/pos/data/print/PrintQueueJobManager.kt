package com.oolio.pos.data.print

import com.oolio.pos.data.db.POSDatabase
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.PrintStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import kotlin.math.min

class PrintQueueJobManager @Inject constructor(
    private val db: POSDatabase,
    private val printerService: PrinterService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {

    private val retryQueue = ConcurrentLinkedQueue<PrintJob>()
    private var isProcessing = false

    suspend fun enqueue(job: PrintJob) {
        db.printJobDao().insertPrintJob(job.copy(status = PrintStatus.PENDING))
        processJob(job)
    }

    suspend fun processJobById(jobId: String) {
        val job = db.printJobDao().getPrintJobById(jobId)
        if (job != null) processJob(job)
    }

    suspend fun processPendingJobs(){
        if(isProcessing) return
        isProcessing = true
        try {
            val pending = db.printJobDao().getAllPendingJobs()
            for (job in pending) {
                processJob(job)
            }
        }finally {
          isProcessing = false
        }
    }

    private fun processJob(job: PrintJob) {
        scope.launch {
            val printerId = job.printerId
            if(printerService.isAvailable(printerId)){
                try {
                    db.printJobDao().updatePrintJobStatus(job.id, PrintStatus.PRINTING.name)
                    val printerStatus  = printerService.print(printerId, job.payloadJson)
                    db.printJobDao().updatePrintJobStatus(job.id, printerStatus)
                }catch(e: Exception) {
                    scheduleRetry(job, e)
                }
            }else{
                db.printJobDao().updatePrintJobStatus(job.id, PrintStatus.PENDING.name)
            }
        }
    }

    private fun scheduleRetry(job: PrintJob, exception: Exception) {
        scope.launch {
            val updateJob = job.copy(
                status = PrintStatus.PENDING,
                attempts = job.attempts + 1,
                lastAttemptedAt = System.currentTimeMillis()
            )
            db.printJobDao().updatePrintJob(updateJob)
            val delayMS = calculateRetryDelay(job.attempts)
            retryQueue.add(updateJob)
            delay(delayMS)
            retryQueue.poll()?.let {
                processJob(job);
            }
        }
    }

    private fun calculateRetryDelay(attempts: Int): Long {
        val base = 2000L
        val max = 60000L
        return min(base* (1 shl attempts), max)
    }

   fun onPrinterAvailable(){
        scope.launch {
            processPendingJobs()
        }
    }


}