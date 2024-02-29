package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

interface SimpleSingleSlotStorage<T> : SingleSlotStorage<T> {
    override var resource: T
    override var amount: Long
}