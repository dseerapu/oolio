package com.oolio.pos.domain

import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order

class ResolveConflictUseCase {

    fun resolveOrder(local: Order, remote: Order): Order {
        return if (remote.updatedAt > local.updatedAt) remote else local
    }

    fun resolveInventory(local: Inventory, remote: Inventory): Inventory {
        return if (remote.updatedAt > local.updatedAt) remote else local
    }

}