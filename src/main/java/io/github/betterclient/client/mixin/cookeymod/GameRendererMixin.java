package io.github.betterclient.client.mixin.cookeymod;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow protected abstract void bobView(MatrixStack poseStack, float f);

    @Shadow @Final private MinecraftClient client;
    BooleanSetting disableCameraBobbing = CookeyMod.get().disableCameraBobbing;
    BooleanSetting enableDamageCameraTilt = CookeyMod.get().enableDamageCameraTilt;
    BooleanSetting alternativeBobbing = CookeyMod.get().alternativeBobbing;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    public void cancelCameraShake(GameRenderer instance, MatrixStack matrixStack, float f) {
        if (!this.disableCameraBobbing.isValue()) {
            this.bobView(matrixStack, f);
        }
    }

    @Redirect(method = "bobViewWhenHurt", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I", opcode = Opcodes.GETFIELD))
    public int modifyBobHurt(LivingEntity instance) {
        if (this.enableDamageCameraTilt.isValue() && instance instanceof ClientPlayerEntity) {
            int hurtTime = instance.hurtTime + 1;
            return hurtTime > instance.maxHurtTime ? 0 : hurtTime;
        }
        return instance.hurtTime;
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void changeToAlternativeBob(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (alternativeBobbing.isValue()) {
            this.alternativeBobView(matrixStack, f);
            ci.cancel();
        }
    }

    private void alternativeBobView(MatrixStack poseStack, float f) {
        if (this.client.getCameraEntity() instanceof PlayerEntity player) {
            float g = player.horizontalSpeed - player.prevHorizontalSpeed;
            float h = -(player.horizontalSpeed + g * f);
            float i = MathHelper.lerp(f, player.prevStrideDistance, player.strideDistance);
            poseStack.translate(MathHelper.sin(h * 3.1415927F) * i * 0.5F, -Math.abs(MathHelper.cos(h * 3.1415927F) * i), 0.0D);
            poseStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(MathHelper.cos(h * 3.1415927F) * i * 3.0F));
        }
    }
}
