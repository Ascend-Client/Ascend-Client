package io.github.betterclient.version.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.impl.other.CrosshairMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At(value = "TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V")))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Ascend.getInstance().bus.call(new RenderEvent());
    }

    private CrosshairMod cross = (CrosshairMod) Ascend.getInstance().moduleManager.getModuleByName("Crosshair");

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCross(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(cross.toggled) {
            if(MinecraftClient.getInstance().crosshairTarget != null &&
                    MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult ehr &&
                    ehr.getType() == HitResult.Type.ENTITY &&
                    MinecraftClient.getInstance().player != null &&
                    ehr.getEntity() != null && cross.render())
                ci.cancel();
            RenderSystem.disableBlend();
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    private void disableCrossColor(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(cross.toggled)
            RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
