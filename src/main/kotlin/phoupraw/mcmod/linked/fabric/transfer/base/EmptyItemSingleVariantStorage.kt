package phoupraw.mcmod.linked.fabric.transfer.base

import com.google.common.collect.Iterators
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.minecraft.plus

abstract class EmptyItemSingleVariantStorage<T : TransferVariant<*>>(context: ContainerItemContext, val storableResource: T, val storableAmount: Long, val filled: ItemVariant) : ItemSingleSlotStorage<T>(context), InsertionOnlyStorage<T> {
    override fun getResource(currentVariant: ItemVariant): T = blankResource
    override fun getAmount(currentVariant: ItemVariant): Long = 0
    override fun getCapacity(currentVariant: ItemVariant, variant: T): Long = storableAmount
    override fun getUpdatedVariant(currentVariant: ItemVariant, newResource: T, newAmount: Long): ItemVariant = if (newAmount == 0L) currentVariant else ItemVariant(filled.item, currentVariant.nbt + filled.nbt)
    override fun supportsExtraction(): Boolean = false
    override fun iterator(): Iterator<SingleSlotStorage<T>> = Iterators.singletonIterator(this)
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = if (maxAmount < storableAmount) 0 else super.insert(resource, storableAmount, transaction)
    override fun canInsert(resource: T): Boolean = resource == storableResource
}
