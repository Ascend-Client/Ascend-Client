package io.github.betterclient.version.mixin.cookeymod.client.entity;

import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.version.mixin.cookeymod.client.MinecraftClientAccessor;
import io.github.betterclient.version.mods.CookeyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    BooleanSetting showEatingInThirdPerson = CookeyMod.get().showEatingInThirdPerson;

    @Inject(method = "method_30154", at = @At("HEAD"), cancellable = true)
    public void addRightEatAnimation(T livingEntity, CallbackInfo ci) {
        Arm usedHand = livingEntity.getActiveHand() == Hand.MAIN_HAND
                ? livingEntity.getMainArm()
                : livingEntity.getMainArm().getOpposite();

        if (this.showEatingInThirdPerson.isValue()) {
            if (livingEntity.isUsingItem() && usedHand == Arm.RIGHT && (livingEntity.getActiveItem().getUseAction() == UseAction.EAT || livingEntity.getActiveItem().getUseAction() == UseAction.DRINK)) {
                boolean run = this.applyEatingAnimation(livingEntity, usedHand, ((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter().tickDelta);
                if (run) ci.cancel();
            }
        }
    }

    @Inject(method = "method_30155", at = @At("HEAD"), cancellable = true)
    public void addLeftEatAnimation(T livingEntity, CallbackInfo ci) {
        Arm usedHand = livingEntity.getActiveHand() == Hand.MAIN_HAND
                ? livingEntity.getMainArm()
                : livingEntity.getMainArm().getOpposite();

        if (this.showEatingInThirdPerson.isValue()) {
            if (livingEntity.isUsingItem() && usedHand == Arm.LEFT && (livingEntity.getActiveItem().getUseAction() == UseAction.EAT || livingEntity.getActiveItem().getUseAction() == UseAction.DRINK)) {
                boolean run = this.applyEatingAnimation(livingEntity, usedHand, ((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter().tickDelta);
                if (run) ci.cancel();
            }
        }
    }

    // Animation values and "formula" from ItemInHandRenderer's applyEatAnimation
    public boolean applyEatingAnimation(LivingEntity livingEntity, Arm humanoidArm, float f) {
        int side = humanoidArm == Arm.RIGHT ? 1 : -1;
        float xRot = humanoidArm == Arm.RIGHT ? this.rightArm.pitch : this.leftArm.pitch;
        float yRot;

        float g = livingEntity.getItemUseTimeLeft() - f + 1.0F;
        float h = g / livingEntity.getActiveItem().getMaxUseTime();
        float j;
        float k = Math.min(1.0F - (float) Math.pow(h, 27.0D), 1.0F);
        if (h < 0.8F) {
            j = MathHelper.abs(MathHelper.cos(g / 4.0F * 3.1415927F) * 0.25F);
            xRot = xRot * 0.5F - 1.57079633F + j;
        }
        else {
            xRot = k * (xRot * 0.5F - 1.32079633F);
        }

        yRot = side * k * -0.5235988F;

        if (humanoidArm == Arm.RIGHT) {
            this.rightArm.pitch = xRot;
            this.rightArm.yaw = yRot;
        } else {
            this.leftArm.pitch = xRot;
            this.leftArm.yaw = yRot;
        }

        return true;
    }
}
