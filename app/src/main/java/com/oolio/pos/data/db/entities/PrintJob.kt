package com.oolio.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("print_jobs")
data class PrintJob(
    @PrimaryKey val id: String,
    val type: PrintType,
    val payloadJson: String,
    val printerId: String,
    val status : PrintStatus,
    val attempts: Int,
    val createdAt: Long,
    val lastAttemptedAt: Long,
    val updatedAt: Long
)

enum class PrintStatus{PENDING,PRINTING, COMPLETED, FAILED}
enum class PrintType{KITCHEN, BILLING, BAR}
