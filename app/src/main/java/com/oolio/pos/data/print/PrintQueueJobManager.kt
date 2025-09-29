package com.oolio.pos.data.print

import com.oolio.pos.data.db.POSDatabase
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.PrintStatus
import com.oolio.pos.eventbus.EventBus
import com.oolio.pos.eventbus.PrintJobCompletedEvent
import com.oolio.pos.eventbus.PrintJobCreatedEvent
import com.oolio.pos.eventbus.PrinterReadyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/***
 * Responsible for Print related tasks
 * Receives Print events and acts accordingly
 * check @see @[com.oolio.pos.eventbus.PrintJobCompletedEvent] [com.oolio.pos.eventbus.PrinterReadyEvent]
 * process All Printer pending jobs
 */
@Singleton
class PrintQueueJobManager @Inject constructor(
    private val db: POSDatabase,
    private val printerService: PrinterService,
    private val eventBus: EventBus,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {

    init {
        eventBus.subscribe<PrintJobCreatedEvent>(scope) {event ->
            scope.launch {
                enqueue(event.printJob)
            }
        }

        eventBus.subscribe<PrinterReadyEvent>(scope) {
            scope.launch {
                processPendingJobs()
            }
        }

        eventBus.subscribe<PrintJobCompletedEvent>(scope) { event ->
            scope.launch {
                deleteCompletedPrintJob(event.printJob)
            }
        }
    }

    private val retryQueue = ConcurrentLinkedQueue<PrintJob>()
    private var isProcessing = false

    suspend fun enqueue(job: PrintJob) {
        db.printJobDao().insertPrintJob(job.copy(status = PrintStatus.PENDING))
        processJob(job)
    }

    suspend fun deleteCompletedPrintJob(printJob: PrintJob){
        db.printJobDao().deletePrintJob(printJob.id)
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
                }catch(_: Exception) {
                    scheduleRetry(job)
                }
            }else{
                db.printJobDao().updatePrintJobStatus(job.id, PrintStatus.PENDING.name)
            }
        }
    }

    private fun scheduleRetry(job: PrintJob) {
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
}