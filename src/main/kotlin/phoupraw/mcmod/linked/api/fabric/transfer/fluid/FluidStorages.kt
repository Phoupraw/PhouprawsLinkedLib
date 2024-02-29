package phoupraw.mcmod.linked.api.fabric.transfer.fluid

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.item.Item
import phoupraw.mcmod.linked.api.util.SingleApiLookup

object FluidStorages {
    @JvmField
    val ITEM = SingleApiLookup<Item, ContainerItemContext, Storage<FluidVariant>>("fabric:fluid_storage")
}