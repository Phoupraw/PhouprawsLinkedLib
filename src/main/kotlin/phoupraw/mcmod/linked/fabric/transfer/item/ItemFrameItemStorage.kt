package phoupraw.mcmod.linked.fabric.transfer.item

import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemStack

open class ItemFrameItemStorage(open val entity: ItemFrameEntity) : SingleItemStackStorage() {
    override var stack: ItemStack
        get() = entity.heldItemStack
        set(stack) {
            entity.heldItemStack = stack
        }
}

