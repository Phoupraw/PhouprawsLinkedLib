package phoupraw.mcmod.linked.fabric.transfer.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import phoupraw.mcmod.linked.fabric.transfer.base.MappedStorage

class InfCapacityItemStorage : MappedStorage<ItemVariant, InfCapacitySingleItemStorage>() {
    override fun newSlot(resource: ItemVariant): InfCapacitySingleItemStorage = InfCapacitySingleItemStorage()
    //    override fun toString(): String = joinToString(prefix = "[", postfix = "]") { it.toString() }
}
