package io.github.betterclient.version.mixin.client.fixes;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    @Inject(method = "enableBlend", at = @At("TAIL"))
    private static void actuallyEnableBlend(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_BLEND);
    }

    @Inject(method = "disableBlend", at = @At("TAIL"))
    private static void actuallyDisableBlend(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_BLEND);
    }
}
