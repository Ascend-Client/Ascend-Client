package io.github.betterclient.client.mixin.cookeymod;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.util.cookeymod.OverlayRendered;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return 0;
    }

    @Shadow protected abstract float getAnimationProgress(T entity, float tickDelta);

    protected LivingEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void showOwnName(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity == MinecraftClient.getInstance().cameraEntity
        && CookeyMod.get().renderOwnName.isValue()) cir.setReturnValue(true);
    }

    @Redirect(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderWithOverlay(FeatureRenderer<T, EntityModel<T>> instance,
                                  MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers,
                                  int light,
                                  T entity,
                                  float limbAngle,
                                  float limbDistance,
                                  float tickDelta,
                                  float animationProgress,
                                  float headYaw,
                                  float headPitch) {
        if (instance instanceof OverlayRendered) {
            int overlayCoords = getOverlay((T) entity, this.getAnimationProgress(entity, limbDistance));
            ((OverlayRendered<T>) instance).renderWithOverlay(matrices, vertexConsumers, light, (T) entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, overlayCoords);
        }
        else {
            instance.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
    }
}
