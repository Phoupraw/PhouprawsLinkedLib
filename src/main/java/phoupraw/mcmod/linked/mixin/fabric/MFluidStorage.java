package phoupraw.mcmod.linked.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.linked.impl.fabric.transfer.fluid.FluidStorageItemWrapper;

@Mixin(value = FluidStorage.class, remap = false)
class MFluidStorage {
    @SuppressWarnings("unused")
    @Shadow
    @Mutable//必须要@Mutable，否则下方customField的original.call抛运行时错误，因为给final字段赋值只能直接在初始化块里，不能在初始化块调用的方法里。
    @Final
    public static ItemApiLookup<Storage<FluidVariant>, ContainerItemContext> ITEM;
    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/fabricmc/fabric/api/transfer/v1/fluid/FluidStorage;ITEM:Lnet/fabricmc/fabric/api/lookup/v1/item/ItemApiLookup;", opcode = Opcodes.PUTSTATIC))
    private static void customField(ItemApiLookup<Storage<FluidVariant>, ContainerItemContext> value, Operation<ItemApiLookup<Storage<FluidVariant>, ContainerItemContext>> original) {
        original.call(new FluidStorageItemWrapper(value));
    }
    ///**
    // * @author Phoupraw
    // * @reason
    // */
    //@Overwrite
    //public static Event<FluidStorage.CombinedItemApiProvider> combinedItemApiProvider(Item item) {
    //    return FluidStorages.combinedItemApiProvider(item);
    //}
}
