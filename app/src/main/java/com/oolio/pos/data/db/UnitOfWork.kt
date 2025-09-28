package com.oolio.pos.data.db

interface UnitOfWork {
    suspend fun <T> execute(block: suspend () -> T): T
}