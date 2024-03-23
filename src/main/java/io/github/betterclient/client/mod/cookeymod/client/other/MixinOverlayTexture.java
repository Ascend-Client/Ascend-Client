package io.github.betterclient.client.mod.cookeymod.client.other;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.util.cookeymod.OverlayReloadListener;
import io.github.betterclient.client.util.cookeymod.OverlayReloader;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(OverlayTexture.class)
public abstract class MixinOverlayTexture implements OverlayReloader, OverlayReloadListener {
    @Shadow @Final private NativeImageBackedTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyHitColor(CallbackInfo ci) {
        this.reloadOverlay();
        OverlayReloadListener.register(this);
    }

    @Override
    public void onOverlayReload() {
        this.reloadOverlay();
    }

    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (blue << 16) + (green << 8) + red;
    }

    public void reloadOverlay() {
        NativeImage nativeImage = this.texture.getImage();

        for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
                if (i < 8) {
                    Color color = CookeyMod.get().damageColor.getColor();
                    assert nativeImage != null;
                    nativeImage.setPixelColor(j, i, getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                }
            }
        }

        RenderSystem.activeTexture(33985);
        this.texture.upload();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }
}