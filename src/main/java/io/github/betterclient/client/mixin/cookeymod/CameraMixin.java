package io.github.betterclient.client.mixin.cookeymod;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.NumberSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private Entity focusedEntity;

    @Shadow private float cameraY;

    @Shadow public abstract Entity getFocusedEntity();

    @Shadow private float lastCameraY;

    NumberSetting sneakAnimationSpeed = CookeyMod.get().sneakAnimationSpeed;

    @Inject(method = "updateEyeHeight", at = @At("HEAD"), cancellable = true)
    public void disableSneakAnimation(CallbackInfo ci) {
        if ((this.sneakAnimationSpeed.getValue() / 100F) == 0.0 && this.focusedEntity != null) {
            this.cameraY = this.getFocusedEntity().getStandingEyeHeight();
            this.lastCameraY = this.cameraY;
            ci.cancel();
        }
    }

    @Redirect(method = "updateEyeHeight", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/Camera;cameraY:F", opcode = Opcodes.PUTFIELD))
    public void setSneakAnimationSpeed(Camera camera, float value) {
        this.cameraY += (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5F * (this.sneakAnimationSpeed.getValue() / 100F);
    }
}
