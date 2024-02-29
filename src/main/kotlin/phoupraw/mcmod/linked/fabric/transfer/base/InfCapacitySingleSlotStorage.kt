package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant

abstract class InfCapacitySingleSlotStorage<T : TransferVariant<*>> : DirectSingleSlotStorage<T>() {
    override fun getCapacity(resource: T): Long = Long.MAX_VALUE
}