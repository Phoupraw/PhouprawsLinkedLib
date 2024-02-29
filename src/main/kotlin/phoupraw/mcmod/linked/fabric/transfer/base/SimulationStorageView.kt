package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.fabric.transfer.storage.simulateExtract

class SimulationStorageView<T>(val back: StorageView<T>) : StorageView<T> by back {
    override val underlyingView: StorageView<T> get() = back.underlyingView
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = back.simulateExtract(transaction, resource, maxAmount)
}