package io.github.betterclient.version.mixin.cookeymod.entity.renderer;

import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.version.util.cookeymod.OverlayRendered;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HorseArmorFeatureRenderer.class)
public abstract class MixinHorseArmorFeatureRenderer implements OverlayRendered<HorseEntity> {
    @Shadow public abstract void render(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, HorseEntity horse, float f, float g, float h, float j, float k, float l);

    int overlayCoords;
    BooleanSetting showDamageTintOnArmor = CookeyMod.get().showDamageTintOnArmor;

    @ModifyArg(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/HorseEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), index = 3)
    public int modifyOverlayCoords(int previousCoords) {
        boolean show = this.showDamageTintOnArmor.isValue();
        return show ? this.overlayCoords : previousCoords;
    }

    @Override
    public void renderWithOverlay(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, HorseEntity entity, float f, float g, float h, float j, float k, float l, int overlayCoords) {
        this.overlayCoords = overlayCoords;
        this.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
    }
}
