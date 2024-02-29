@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.fabric.transfer.xp

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.item.Item
import phoupraw.mcmod.linked.api.util.SingleApiFinder
import phoupraw.mcmod.linked.fabric.transfer.base.EmptyItemSingleVariantStorage
import phoupraw.mcmod.linked.fabric.transfer.xp.EmptyItemXpStorage

class EmptyItemXpStorage(context: ContainerItemContext, amount: Long, filled: ItemVariant) : EmptyItemSingleVariantStorage<Xp>(context, Xp, amount, filled) {
    override val blankResource: Xp get() = Xp
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = true

    companion object {
        fun finderOf(amount: Long, filled: ItemVariant): SingleApiFinder<Item, ContainerItemContext, Storage<Xp>> = SingleApiFinder { _, context -> EmptyItemXpStorage(context, amount, filled) }
    }
}
