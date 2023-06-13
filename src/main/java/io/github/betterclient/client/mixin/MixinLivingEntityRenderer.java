package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void showOwnName(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity == MinecraftClient.getInstance().cameraEntity && CookeyMod.get().renderOwnName.isValue()) cir.setReturnValue(true);
    }
}
