package com.oolio.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("change_records")
data class ChangeRecord(
    @PrimaryKey(autoGenerate = true) val id : Long=0,
    val entityType : EntityType,
    val operationType: OperationType,
    val payloadJson: String,
    val deviceId: String,
    val clientId : String,
    val clientTs : Long,
    val status: ChangeRecordStatus,
    val attempts: Int,
    val lastAttemptedAt: Long,
    val createdAt: Long,
)

enum class EntityType{ORDERS,INVENTORY}
enum class OperationType{CREATE, UPDATE, DELETE, PATCH}
enum class ChangeRecordStatus{PENDING, SUCCESS, FAILED}
