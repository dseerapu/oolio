package com.oolio.pos.data.print

import com.oolio.pos.data.db.entities.PrintStatus

interface PrinterService {
    fun isAvailable(printerId: String): Boolean
    fun print(printerId: String, data: String): PrintStatus
    fun onPrinterAvailable(printerId: String, callback: (Boolean) -> Unit)
}