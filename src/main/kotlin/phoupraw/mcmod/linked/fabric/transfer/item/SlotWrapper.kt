package phoupraw.mcmod.linked.fabric.transfer.item

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class SlotWrapper(val slot: Slot) : SingleStackStorage() {
    override var stack: ItemStack
        get() = slot.stack
        set(stack) {
            slot.stack = stack
        }
}
