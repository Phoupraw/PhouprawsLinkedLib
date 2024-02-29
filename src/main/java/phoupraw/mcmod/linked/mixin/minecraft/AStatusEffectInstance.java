package phoupraw.mcmod.linked.mixin.minecraft;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatusEffectInstance.class)
public interface AStatusEffectInstance {
    @Accessor
    void setDuration(int value);
}
