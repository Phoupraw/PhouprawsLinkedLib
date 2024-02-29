@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.xp

import com.google.common.collect.Iterators
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp
import phoupraw.mcmod.linked.fabric.transfer.base.ItemSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant

class MendingItemXpStorage /*private constructor*/(context: ContainerItemContext) : ItemSingleSlotStorage<Xp>(context), InsertionOnlyStorage<Xp> {
    override val blankResource: Xp get() = Xp
    override fun getResource(currentVariant: ItemVariant): Xp = Xp
    override fun getAmount(currentVariant: ItemVariant): Long = currentVariant.toStack().run { (maxDamage - damage) / 2 }.toLong()
    override fun getCapacity(currentVariant: ItemVariant, variant: Xp): Long = (currentVariant.toStack().maxDamage / 2).toLong()
    override fun getUpdatedVariant(currentVariant: ItemVariant, newResource: Xp, newAmount: Long): ItemVariant = ItemVariant(currentVariant.toStack().apply { damage = maxDamage - (newAmount * 2).toInt() })
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = amount == 0L
    override fun iterator(): Iterator<SingleSlotStorage<Xp>> = Iterators.singletonIterator(this)
    override fun extract(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long = 0
    override fun supportsExtraction(): Boolean = false
    //    companion object {
    //        operator fun invoke(context: ContainerItemContext): Storage<Xp> = if (EnchantmentHelper.getLevel(Enchantments.MENDING, context.itemVariant.toStack()) != 0) MendingXpStorage(context) else Storage.empty()
    //        @Deprecated("")
    //        fun of(entity: LivingEntity): Storage<Xp> {
    //            return CombinedStorage<Xp, Storage<Xp>>(LivingEntityItemStorage.of(entity).getEquipments().stream().map { slot: SingleSlotStorage<ItemVariant> -> ContainerItemContext.ofSingleSlot(slot) }.map { context: ContainerItemContext -> of(context) }.toList())
    //        }
    //    }
}
