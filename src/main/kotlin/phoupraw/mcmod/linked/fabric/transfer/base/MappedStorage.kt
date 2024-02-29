package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

abstract class MappedStorage<T, S : SingleSlotStorage<T>> : Storage<T> {
    protected val slotMap: MutableMap<T, S> = LinkedHashMap()
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = slotMap.getOrPut(resource) { newSlot(resource) }.insert(resource, maxAmount, transaction)
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = slotMap[resource]?.extract(resource, maxAmount, transaction) ?: 0
    override fun iterator(): Iterator<S> = slotMap.values.iterator()
    abstract fun newSlot(resource: T): S
    override fun toString(): String = slotMap.toString()
}