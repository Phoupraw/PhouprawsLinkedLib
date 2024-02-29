package phoupraw.mcmod.linked.mixin.minecraft;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrbEntity.class)
public interface AExperienceOrbEntity {
    @Accessor
    void setAmount(int amount);
}
