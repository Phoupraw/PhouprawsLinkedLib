package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.fabric.transfer.storage.simulateExtract
import phoupraw.mcmod.linked.fabric.transfer.storage.simulateInsert

open class SimulationStorage<T>(open val back: Storage<T>) : Storage<T> by back {
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = back.simulateExtract(transaction, resource, maxAmount)
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = back.simulateInsert(transaction, resource, maxAmount)
    override fun iterator(): Iterator<StorageView<T>> = back.asSequence().map(::SimulationStorageView).iterator()
}