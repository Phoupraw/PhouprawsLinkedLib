package phoupraw.mcmod.linked.mixin.fabric;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.impl.lookup.item.ItemApiLookupImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ItemApiLookupImpl.class, remap = false)
public interface AItemApiLookupImpl<A, C> {
    @Accessor
    ApiProviderMap<Item, ItemApiLookup.ItemApiProvider<A, C>> getProviderMap();
    @Accessor
    List<ItemApiLookup.ItemApiProvider<A, C>> getFallbackProviders();
}
