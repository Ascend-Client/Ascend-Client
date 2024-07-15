package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.version.util.ScreenLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class MixinDrawContext {
    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V", at = @At("RETURN"))
    public void injection(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, CallbackInfo ci) {
        ((IBridge.MatrixStack) matrices).setCTX(this);
        ScreenLoader.latestDrawContext = (DrawContext) ((Object) this);
    }
}
