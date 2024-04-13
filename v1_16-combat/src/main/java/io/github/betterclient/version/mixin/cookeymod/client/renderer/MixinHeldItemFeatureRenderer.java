package io.github.betterclient.version.mixin.cookeymod.client.renderer;

import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.version.mods.CookeyMod;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MixinHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
    BooleanSetting enableToolBlocking = CookeyMod.get().enableToolBlocking;
    BooleanSetting shieldlesstool = CookeyMod.get().shieldlessToolBlocking;

    public MixinHeldItemFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    public void hideShieldWithToolBlocking(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (this.enableToolBlocking.isValue()) {
            Hand otherHand = arm == entity.getMainArm() ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack otherHandStack = entity.getStackInHand(otherHand);
            if (stack.getItem() instanceof ShieldItem && otherHandStack.getItem() instanceof ToolItem && entity.isBlocking()) {
                ci.cancel();
            }
        }

        if (this.shieldlesstool.isValue()) {
            Hand otherHand = arm == entity.getMainArm() ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack otherHandStack = entity.getStackInHand(otherHand);
            if (!(stack.getItem() instanceof ShieldItem) && otherHandStack.getItem() instanceof ToolItem && CookeyMod.isBlockingRightClick()) {
                ci.cancel();
            }
        }
    }
}
