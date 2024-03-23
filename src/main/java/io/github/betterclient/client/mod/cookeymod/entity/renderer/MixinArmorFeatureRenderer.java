package io.github.betterclient.client.mod.cookeymod.entity.renderer;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.util.cookeymod.OverlayRendered;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> implements OverlayRendered<T> {
    @Shadow public abstract void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l);

    int overlayCoords;
    BooleanSetting showDamageTintOnArmor = CookeyMod.get().showDamageTintOnArmor;

    @ModifyArg(method = "renderArmorParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), index = 3)
    public int modifyOverlayCoords(int previousCoords) {
        boolean show = this.showDamageTintOnArmor.isValue();
        return show ? this.overlayCoords : previousCoords;
    }

    @Override
    public void renderWithOverlay(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, int overlayCoords) {
        this.overlayCoords = overlayCoords;
        this.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
    }
}
