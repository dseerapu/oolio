package com.oolio.pos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.entities.ChangeRecordStatus

@Dao
interface ChangeRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRecord(changeRecord: ChangeRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRecords(changeRecords: List<ChangeRecord>)

    @Upsert
    suspend fun upsertChangeRecord(changeRecord: ChangeRecord)

    @Query("SELECT * FROM change_records WHERE status = :status")
    suspend fun getChangeRecordsByStatus(status: ChangeRecordStatus): List<ChangeRecord>

}