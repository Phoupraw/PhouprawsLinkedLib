package phoupraw.mcmod.linked.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kotlin.jvm.functions.Function2;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.fluid.CombinedProvidersImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.linked.api.fabric.transfer.fluid.FluidStorages;
import phoupraw.mcmod.linked.impl.fabric.transfer.fluid.FluidStorageItemWrapper;

@Mixin(CombinedProvidersImpl.class)
abstract class MCombinedProvidersImpl {
    @WrapOperation(method = "getOrCreateItemEvent", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/lookup/v1/item/ItemApiLookup;getProvider(Lnet/minecraft/item/Item;)Lnet/fabricmc/fabric/api/lookup/v1/item/ItemApiLookup$ItemApiProvider;"))
    private static ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext> combined(ItemApiLookup<Storage<FluidVariant>, ContainerItemContext> instance, Item item, Operation<ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext>> original) {
        for (Function2<Item, ContainerItemContext, Storage<FluidVariant>> find : FluidStorages.ITEM.get(item).callbacks().values()) {
            if (find instanceof FluidStorageItemWrapper.ProviderWrapper wrapper && wrapper.back() instanceof CombinedProvidersImpl.Provider provider) {
                return provider;
            }
        }
        var provider = new CombinedProvidersImpl.Provider();
        FluidStorage.ITEM.registerForItems(provider, item);
        return provider;
    }
}
