package io.github.betterclient.version.mixin.cookeymod.client.renderer;

import io.github.betterclient.version.mods.CookeyMod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {
    @Shadow
    private ItemStack mainHand;

    @Shadow
    private ItemStack offHand;

    @Shadow protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    public void onRenderArmWithItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (CookeyMod.get().onlyShowShieldWhenBlocking.isValue() || CookeyMod.get().enableToolBlocking.isValue()) {
            if (item.getItem() instanceof ShieldItem && !(!player.getActiveItem().isEmpty() && player.getActiveItem().getItem() instanceof ShieldItem)) {
                ci.cancel();
            }
        }
        if (CookeyMod.get().enableToolBlocking.isValue()) {
            ItemStack otherHandItem = hand == Hand.MAIN_HAND ? this.offHand : this.mainHand;
            if (item.getItem() instanceof ShieldItem) {
                if (otherHandItem.getItem() instanceof ToolItem && player.isBlocking()) {
                    ci.cancel();
                }
            }
            if (player.getActiveHand() != hand && player.isBlocking() && item.getItem() instanceof ToolItem) {
                matrices.push();
                Arm humanoidArm = hand == Hand.MAIN_HAND
                        ? player.getMainArm()
                        : player.getMainArm().getOpposite();
                this.applyEquipOffset(matrices, humanoidArm, equipProgress);
                this.applyItemBlockTransform(matrices, humanoidArm);
                if (CookeyMod.get().swingAndUseItem.isValue()) {
                    this.applySwingOffset(matrices, humanoidArm, swingProgress);
                }
                boolean isRightHand = humanoidArm == Arm.RIGHT;
                this.renderItem(player, item, isRightHand ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !isRightHand, matrices, vertexConsumers, light);

                matrices.pop();
                ci.cancel();
            }
        }

        if (CookeyMod.get().shieldlessToolBlocking.isValue()) {
            ItemStack otherHandItem = hand == Hand.MAIN_HAND ? this.offHand : this.mainHand;
            if (!(item.getItem() instanceof ShieldItem)) {
                if (otherHandItem.getItem() instanceof ToolItem && CookeyMod.isBlockingRightClick()) {
                    ci.cancel();
                }
            }
            if (player.getActiveHand() != hand && CookeyMod.isBlockingRightClick() && item.getItem() instanceof ToolItem) {
                matrices.push();
                Arm humanoidArm = hand == Hand.MAIN_HAND
                        ? player.getMainArm()
                        : player.getMainArm().getOpposite();
                this.applyEquipOffset(matrices, humanoidArm, equipProgress);
                this.applyItemBlockTransform(matrices, humanoidArm);
                if (CookeyMod.get().swingAndUseItem.isValue()) {
                    this.applySwingOffset(matrices, humanoidArm, swingProgress);
                }
                boolean isRightHand = humanoidArm == Arm.RIGHT;
                this.renderItem(player, item, isRightHand ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !isRightHand, matrices, vertexConsumers, light);

                matrices.pop();
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applySwingOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
                    ordinal = 1))
    public void cancelAttackTransform(HeldItemRenderer instance, MatrixStack matrices, Arm arm, float swingProgress) {
        if (!CookeyMod.get().swingAndUseItem.isValue())
            this.applySwingOffset(matrices, arm, swingProgress);
    }

    @Inject(method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void injectAttackTransform(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Arm humanoidArm = hand == Hand.MAIN_HAND
                ? player.getMainArm()
                : player.getMainArm().getOpposite();
        if (CookeyMod.get().swingAndUseItem.isValue() && !player.isUsingRiptide()) {
            this.applySwingOffset(matrices, humanoidArm, swingProgress);
        }
    }

    @ModifyVariable(method = "updateHeldItems", slice = @Slice(
            from = @At(value = "JUMP", ordinal = 3)
    ), at = @At(value = "FIELD", ordinal = 0))
    public float modifyArmHeight(float f) {
        double offset = CookeyMod.get().attackCooldownHandOffset.getValue() / 100D;
        return (float) (f * (1 - offset) + offset);
    }

    /* Values from 15w33b, thanks to Fuzss for providing them
    https://github.com/Fuzss/swordblockingcombat/blob/1.15/src/main/java/com/fuzs/swordblockingcombat/client/handler/RenderBlockingHandler.java
     */
    public void applyItemBlockTransform(MatrixStack poseStack, Arm humanoidArm) {
        int reverse = humanoidArm == Arm.RIGHT ? 1 : -1;
        poseStack.translate(reverse * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-102.25F));
        poseStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(reverse * 13.365F));
        poseStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(reverse * 78.05F));
    }
}
