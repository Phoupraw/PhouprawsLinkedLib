package phoupraw.mcmod.linked.fabric.transfer.item

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ItemScatterer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.fabric.transfer.storage.InventoryStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.empty
import phoupraw.mcmod.linked.lang.GenericApiLookup

object ItemStorages {
    @JvmField
    val ITEM: GenericApiLookup<Item, Storage<ItemVariant>, ContainerItemContext> = GenericApiLookup("item")

    init {
        ITEM[Items.SHULKER_BOX].register { _, context ->
            context.itemVariant.nbt?.getCompound("BlockEntityTag")?.let { InventoryStorage(NbtSimpleInventory(it, 27)) }
        }
    }
    @JvmStatic
    fun scatter(world: World, pos: BlockPos, storage: Iterable<StorageView<ItemVariant>>) {
        val list = DefaultedList.of<ItemStack>()
        for (view in storage) {
            if (view.empty) continue
            var amount = view.amount
            val resource = view.resource
            while (amount > 64) {
                list.add(resource.toStack(64))
                amount -= 64
            }
            list.add(resource.toStack(amount.toInt()))
        }
        ItemScatterer.spawn(world, pos, list)
    }
}