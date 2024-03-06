package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.minecraft.plus

abstract class FullItemSingleVariantStorage<T : TransferVariant<*>>(context: ContainerItemContext, open val storedResource: T, open val storedAmount: Long, open val emptied: ItemVariant = ItemVariant()) : ItemSingleSlotStorage<T>(context), ExtractionOnlyStorage<T> {
    override fun getResource(currentVariant: ItemVariant): T = storedResource
    override fun getAmount(currentVariant: ItemVariant): Long = storedAmount
    override fun getCapacity(currentVariant: ItemVariant, variant: T): Long = storedAmount
    override fun getUpdatedVariant(currentVariant: ItemVariant, newResource: T, newAmount: Long): ItemVariant = if (newAmount == 0L) ItemVariant(emptied.item, currentVariant.nbt + emptied.nbt) else currentVariant
    override fun supportsInsertion(): Boolean = false
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long = if (maxAmount < storedAmount) 0 else super.extract(resource, storedAmount, transaction)
}
