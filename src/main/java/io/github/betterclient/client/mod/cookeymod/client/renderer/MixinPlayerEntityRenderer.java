package io.github.betterclient.client.mod.cookeymod.client.renderer;

import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final BooleanSetting enableToolBlocking = CookeyMod.get().enableToolBlocking;
    private static final BooleanSetting shieldlessToolBlocking = CookeyMod.get().shieldlessToolBlocking;

    public MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(dispatcher, model, shadowRadius);
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void addItemBlockPose(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (enableToolBlocking.isValue()) {
            ItemStack currentHandStack = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
            ItemStack otherHandStack = hand == Hand.MAIN_HAND ? itemStack2 : itemStack;
            if (abstractClientPlayerEntity.isBlocking()) {
                if (currentHandStack.getItem() instanceof ToolItem && otherHandStack.getItem() instanceof ShieldItem) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
                } else if (currentHandStack.getItem() instanceof ShieldItem && otherHandStack.getItem() instanceof ToolItem) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
                }
            }
        }

        if (shieldlessToolBlocking.isValue()) {
            ItemStack currentHandStack = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
            ItemStack otherHandStack = hand == Hand.MAIN_HAND ? itemStack2 : itemStack;
            if(CookeyMod.isBlockingRightClick()) {
                if (currentHandStack.getItem() instanceof ToolItem && !(otherHandStack.getItem() instanceof ShieldItem)) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
                }
            }
        }
    }
}
