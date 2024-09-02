package io.github.betterclient.version.mixin.client.gui;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.impl.other.CrosshairMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At(value = "TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")))
    public void render(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
        Ascend.getInstance().bus.call(new RenderEvent());
    }
    @Unique
    private final CrosshairMod cross = (CrosshairMod) Ascend.getInstance().moduleManager.getModuleByName("Crosshair");

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCross(MatrixStack matrices, CallbackInfo callback) {
        if(cross.toggled) {
            if(MinecraftClient.getInstance().crosshairTarget != null &&
                    MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult ehr &&
                    ehr.getType() == HitResult.Type.ENTITY &&
                    MinecraftClient.getInstance().player != null &&
                    ehr.getEntity() != null &&

                    ehr.method_31252() <= MinecraftClient.getInstance().player.method_31239(0f) &&
                    cross.render())
                callback.cancel();
            RenderSystem.disableBlend();
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    private void disableCrossColor(MatrixStack matrices, CallbackInfo ci) {
        if(cross.toggled)
            RenderSystem.enableBlend();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
    }
}