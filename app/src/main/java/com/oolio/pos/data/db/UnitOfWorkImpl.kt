package com.oolio.pos.data.db

import androidx.room.withTransaction

class UnitOfWorkImpl(private val db: POSDatabase) : UnitOfWork {
    override suspend fun <T> execute(block: suspend () -> T): T {
        return db.withTransaction { block() }
    }
}