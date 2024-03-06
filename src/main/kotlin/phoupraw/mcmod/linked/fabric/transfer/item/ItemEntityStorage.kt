package phoupraw.mcmod.linked.fabric.transfer.item

import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack

open class ItemEntityStorage(val itemEntity: ItemEntity) : SingleItemStackStorage() {
    override var stack: ItemStack
        get() = itemEntity.stack
        set(stack) {
            itemEntity.stack = stack
        }
}
