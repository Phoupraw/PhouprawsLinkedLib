package phoupraw.mcmod.linked.fabric.transfer.item

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class EquipmentSlotItemStorage(val entity: LivingEntity, val slot: EquipmentSlot) : SingleItemStackStorage() {
    override var stack: ItemStack
        get() = entity.getEquippedStack(slot)
        set(value) {
            entity.equipStack(slot, value)
        }
}
