package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.ItemTweaks;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "getHandSwingProgress", at = @At(value = "RETURN", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void changeAttackAnim(float f, CallbackInfoReturnable<Float> cir, float g, float h) {
        if (ItemTweaks.get().oldSwing.isValue() && ItemTweaks.get().isToggled()) {
            cir.setReturnValue(h);
        }
    }
}