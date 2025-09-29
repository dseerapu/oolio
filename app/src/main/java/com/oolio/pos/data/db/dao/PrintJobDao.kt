package com.oolio.pos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.PrintStatus

@Dao
interface PrintJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintJob(printJob: PrintJob)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintJobs(printJobs: List<PrintJob>)

    @Query("SELECT * FROM print_jobs")
    suspend fun getAllPrintJobs(): List<PrintJob>

    @Upsert
    suspend fun updatePrintJob(printJob: PrintJob)

    @Query("SELECT * FROM print_jobs where status = 'PENDING'")
    suspend fun getAllPendingJobs(): List<PrintJob>

    @Query("SELECT * FROM print_jobs WHERE id = :id")
    suspend fun getPrintJobById(id: String): PrintJob?

    @Query("UPDATE print_jobs SET status = :status WHERE id = :id")
    suspend fun updatePrintJobStatus(id: String, status: String)

    @Query("UPDATE print_jobs SET attempts = :attempts WHERE id = :id")
    suspend fun updatePrintJobAttempts(id: String, attempts: Int)

    @Query("UPDATE print_jobs SET lastAttemptedAt = :lastAttemptedAt WHERE id = :id")
    suspend fun updatePrintJobLastAttemptedAt(id: String, lastAttemptedAt: Long)

    @Query("UPDATE print_jobs SET status = :status WHERE id = :id")
    suspend fun updatePrintJobStatus(id: String, status: PrintStatus)

    @Query("DELETE FROM print_jobs WHERE status = 'COMPLETED' AND createdAt < :cutoff")
    suspend fun deleteOldCompleted(cutoff: Long)

    @Query("DELETE FROM print_jobs WHERE status = 'FAILED' AND createdAt < :cutoff")
    suspend fun deleteOldFailed(cutoff: Long)

    @Query("DELETE FROM print_jobs WHERE id = :id")
    suspend fun deletePrintJob(id: String)

}