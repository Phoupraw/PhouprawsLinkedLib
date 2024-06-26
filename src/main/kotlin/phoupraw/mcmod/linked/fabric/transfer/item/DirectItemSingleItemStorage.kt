package phoupraw.mcmod.linked.fabric.transfer.item

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.minecraft.nbt.NbtCompound
import org.jetbrains.annotations.ApiStatus.Experimental
import phoupraw.mcmod.linked.fabric.transfer.base.ItemSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.base.SimpleSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemResourceAmount
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.fabric.transfer.storage.NbtCompound
import phoupraw.mcmod.linked.minecraft.clean
import phoupraw.mcmod.linked.minecraft.get
import phoupraw.mcmod.linked.minecraft.put

@Experimental
abstract class DirectItemSingleItemStorage(context: ContainerItemContext) : ItemSingleSlotStorage<ItemVariant>(context), SimpleSingleSlotStorage<ItemVariant> {
    abstract val path: List<String>
    override val blankResource: ItemVariant get() = ItemVariant()
    override var amount: Long
        get() = super.amount
        set(value) {
            getUpdatedVariant(context.itemVariant, resource, value)
        }
    override var resource: ItemVariant
        get() = super.resource
        set(value) {
            getUpdatedVariant(context.itemVariant, value, amount)
        }
    override fun getResource(currentVariant: ItemVariant): ItemVariant = getResouceAmount(currentVariant)?.resource ?: blankResource
    override fun getAmount(currentVariant: ItemVariant): Long = if (resourceBlank) 0 else getResouceAmount(currentVariant)?.amount ?: 1
    open fun getResouceAmount(currentVariant: ItemVariant): ResourceAmount<ItemVariant>? = (currentVariant.nbt?.get(path) as? NbtCompound)?.let { ItemResourceAmount(it) }
    override fun getUpdatedVariant(currentVariant: ItemVariant, newResource: ItemVariant, newAmount: Long): ItemVariant = ItemVariant(currentVariant.item, currentVariant.copyOrCreateNbt().apply { put(path, NbtCompound(newResource, newAmount)) }.clean())

}
