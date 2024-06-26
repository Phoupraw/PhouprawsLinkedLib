package phoupraw.mcmod.linked.fabric.transfer.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import phoupraw.mcmod.linked.fabric.transfer.base.InfCapacitySingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant

open class InfCapacitySingleItemStorage @JvmOverloads constructor(resource: ItemVariant = ItemVariant(), amount: Long = if (resource.blank) 0 else 1) : InfCapacitySingleSlotStorage<ItemVariant>() {
    init {
        this.resource = resource
        this.amount = amount
    }

    override val blankVariant: ItemVariant
        get() = ItemVariant()
}
