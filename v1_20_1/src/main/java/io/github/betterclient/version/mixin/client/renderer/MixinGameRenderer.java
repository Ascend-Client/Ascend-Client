package io.github.betterclient.version.mixin.client.renderer;

import io.github.betterclient.client.mod.impl.other.MotionBlur;
import io.github.betterclient.client.mod.impl.other.NoHurtCam;
import io.github.betterclient.client.mod.impl.other.SuperSecretSettings;
import io.github.betterclient.client.mod.impl.other.Zoom;
import io.github.betterclient.fabric.relocate.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void onRenderScreen(Screen instance, DrawContext context, int x, int y, float v) {
        Screen renderingScreen = this.client.currentScreen;
        if(FabricLoader.getInstance().isModLoaded("fabric-screen-api-v1")) {
            try {
                Class<?> screenEventsClass = Class.forName("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents");
                Method beforeRenderMethod = screenEventsClass.getMethod("beforeRender", Screen.class);
                Object beforeRenderObj = beforeRenderMethod.invoke(null, renderingScreen);

                Class<?> eventsClass = Class.forName("net.fabricmc.fabric.api.event.Event");
                Method invokerMethod = eventsClass.getMethod("invoker");

                Object invoker = invokerMethod.invoke(beforeRenderObj);
                Class<?> screenEventsBeforeRender = Class.forName("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$BeforeRender");
                Method beforeRenderToCall = screenEventsBeforeRender.getMethod("beforeRender", Screen.class, DrawContext.class, int.class, int.class, float.class);

                beforeRenderToCall.invoke(invoker, instance, context, x, y, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        instance.renderWithTooltip(context, x, y, v);

        if(FabricLoader.getInstance().isModLoaded("fabric-screen-api-v1")) {
            try {
                Class<?> screenEventsClass = Class.forName("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents");
                Method afterRenderMethod = screenEventsClass.getMethod("afterRender", Screen.class);
                Object afterRenderObj = afterRenderMethod.invoke(null, renderingScreen);

                Class<?> eventsClass = Class.forName("net.fabricmc.fabric.api.event.Event");
                Method invokerMethod = eventsClass.getMethod("invoker");

                Object invoker = invokerMethod.invoke(afterRenderObj);
                Class<?> screenEventsAfterRender = Class.forName("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterRender");
                Method afterRenderToCall = screenEventsAfterRender.getMethod("afterRender", Screen.class, DrawContext.class, int.class, int.class, float.class);

                afterRenderToCall.invoke(invoker, instance, context, x, y, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}