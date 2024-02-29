package phoupraw.mcmod.linked.impl.fabric.transfer.fluid

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import phoupraw.mcmod.linked.api.util.SingleApiFinder
import phoupraw.mcmod.linked.fabric.transfer.fluid.FluidStorages
import phoupraw.mcmod.linked.lang.GenericApiProvider

@Suppress("NonExtendableApiUsage")
class FluidStorageItemWrapper(val back: ItemApiLookup<Storage<FluidVariant>, ContainerItemContext>) : ItemApiLookup<Storage<FluidVariant>, ContainerItemContext> by back {
    override fun find(itemStack: ItemStack, context: ContainerItemContext): Storage<FluidVariant>? = FluidStorages.ITEM(itemStack.item, context)
    override fun getProvider(item: Item): ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext> {
        return ItemApiLookup.ItemApiProvider { itemStack, context ->
            FluidStorages.ITEM[itemStack.item].call(itemStack.item, context)
            phoupraw.mcmod.linked.api.fabric.transfer.fluid.FluidStorages.ITEM[itemStack.item].call(itemStack.item, context)
        }
    }

    override fun registerFallback(fallbackProvider: ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext>) {
        FluidStorages.ITEM.fallback.register(ProviderWrapper(fallbackProvider))
        phoupraw.mcmod.linked.api.fabric.transfer.fluid.FluidStorages.ITEM.fallback.register(ProviderWrapper(fallbackProvider))
    }

    override fun registerForItems(provider: ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext>, vararg items: ItemConvertible) {
        for (item in items) {
            FluidStorages.ITEM[item.asItem()].register(ProviderWrapper(provider))
            phoupraw.mcmod.linked.api.fabric.transfer.fluid.FluidStorages.ITEM[item.asItem()].register(ProviderWrapper(provider))
        }
    }

    internal class ProviderWrapper(@get:JvmName("back") val back: ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext>) : GenericApiProvider<Item, Storage<FluidVariant>, ContainerItemContext>, SingleApiFinder<Item, ContainerItemContext, Storage<FluidVariant>> {
        override fun invoke(key: Item, context: ContainerItemContext): Storage<FluidVariant>? = back.find(context.itemVariant.toStack(), context)
    }
}