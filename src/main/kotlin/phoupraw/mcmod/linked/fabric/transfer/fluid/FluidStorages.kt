package phoupraw.mcmod.linked.fabric.transfer.fluid

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.item.Item
import phoupraw.mcmod.linked.lang.GenericApiLookup

@Deprecated("换API", ReplaceWith(""))
object FluidStorages {
    @JvmField
    val ITEM = GenericApiLookup<Item, Storage<FluidVariant>, ContainerItemContext>("fabric:fluid_storage")
}