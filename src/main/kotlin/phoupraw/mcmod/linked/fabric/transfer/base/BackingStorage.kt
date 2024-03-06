package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

open class BackingStorage<T, out S : Storage<T>>(open val back: S) : Storage<T> by back {
    //FIXME iterator
    class InsertionOnly<T, out S : Storage<T>>(back: S) : BackingStorage<T, S>(back), InsertionOnlyStorage<T> {
        override fun supportsExtraction(): Boolean = false
        override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
        override fun iterator(): Iterator<StorageView<T>> = back.iterator()
    }

    class ExtractionOnly<T, out S : Storage<T>>(back: S) : BackingStorage<T, S>(back), ExtractionOnlyStorage<T> {
        override fun supportsInsertion(): Boolean = false
        override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
    }

    class ReadOnly<T, out S : Storage<T>>(back: S) : BackingStorage<T, S>(back), InsertionOnlyStorage<T>, ExtractionOnlyStorage<T> {
        override fun supportsExtraction(): Boolean = false
        override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
        override fun supportsInsertion(): Boolean = false
        override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
        override fun iterator(): Iterator<StorageView<T>> = back.iterator()
    }
}