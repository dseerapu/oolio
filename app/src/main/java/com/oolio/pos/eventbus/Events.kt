package com.oolio.pos.eventbus

import com.oolio.pos.data.db.entities.PrintJob

data class OrderCreatedEvent(val orderId: String)
data class PrintJobCompletedEvent(val printJob: PrintJob )
data class PrintJobCreatedEvent(val printJob: PrintJob)
class PrinterReadyEvent()