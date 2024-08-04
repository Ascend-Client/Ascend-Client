package io.github.betterclient.version.mixin.client.renderer;

import io.github.betterclient.client.mod.impl.other.MotionBlur;
import io.github.betterclient.client.mod.impl.other.NoHurtCam;
import io.github.betterclient.client.mod.impl.other.SuperSecretSettings;
import io.github.betterclient.client.mod.impl.other.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
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

    @ModifyConstant(method = "tiltViewWhenHurt", constant = @Constant(doubleValue = 14.0))
    public double changeArg(double fourTeen) {
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

            if(blur.shader != null)
                ((PostEffectProcessor) blur.shader).render(tickDelta);
        }

        if(SuperSecretSettings.get().toggled) {
            SuperSecretSettings.get().onUpdate();
            if(SuperSecretSettings.get().shader != null)
                ((PostEffectProcessor) SuperSecretSettings.get().shader).render(client.getTickDelta());
        }

        this.client.getProfiler().pop();
    }
}