@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.xp

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.item.Item
import phoupraw.mcmod.linked.api.fabric.transfer.xp.EmptyItemXpStorage
import phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp
import phoupraw.mcmod.linked.fabric.transfer.base.EmptyItemSingleVariantStorage
import phoupraw.mcmod.linked.lang.GenericApiProvider

class EmptyItemXpStorage(context: ContainerItemContext, amount: Long, filled: ItemVariant) : EmptyItemSingleVariantStorage<Xp>(context, Xp, amount, filled) {
    override val blankResource: Xp get() = Xp
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = amount == 0L

    companion object {
        operator fun invoke(amount: Long, filled: ItemVariant): GenericApiProvider<Item, Storage<Xp>, ContainerItemContext> = { _, context -> EmptyItemXpStorage(context, amount, filled) }
    }
}
