package io.github.betterclient.client.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.mod.impl.other.MotionBlur;
import io.github.betterclient.client.mod.impl.other.NoHurtCam;
import io.github.betterclient.client.mod.impl.other.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "getFov", cancellable = true)
    public void onGetFOVModifier(CallbackInfoReturnable<Double> info) {

        if(!Zoom.get().toggled)
            return;

        double defaultFOV = info.getReturnValue();
        info.setReturnValue(Zoom.get().handleZoom(defaultFOV));

    }

    @ModifyConstant(method = "tiltViewWhenHurt", constant = @Constant(floatValue = 14.0F))
    public float changeArg(float fourTeen) {
        if(NoHurtCam.get().isToggled()) {
            return 14 * (NoHurtCam.get().setting.value / 100F);
        } else {
            return fourTeen;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lnet/minecraft/client/gl/Framebuffer;"))
    public void wefwefewfwef(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        this.client.getProfiler().push("Motion Blur");

        if (MotionBlur.get().toggled) {
            MotionBlur blur = MotionBlur.get();
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        this.client.getProfiler().pop();
    }
}