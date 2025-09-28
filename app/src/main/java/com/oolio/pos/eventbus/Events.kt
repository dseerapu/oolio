package com.oolio.pos.eventbus

data class OrderCreatedEvent(val orderId: String)
data class PrintJobCompletedEvent(val jobId: String, val isSuccess: Boolean )