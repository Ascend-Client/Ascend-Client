package io.github.betterclient.version.mixin.cookeymod.entity;

import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    @Shadow public abstract float getAttackCooldownProgress(float f);

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World level) {
        super(entityType, level);
    }

    BooleanSetting force100 = CookeyMod.get().force100PercentRecharge;

    @Inject(method = "method_31238", at = @At(value = "RETURN"), cancellable = true)
    public void disable4TickSwinging(float f, CallbackInfoReturnable<Boolean> cir) {
        if (this.getAttackCooldownProgress(f) < 1.0F && this.force100.isValue()) {
            cir.setReturnValue(false);
        }
    }
}
