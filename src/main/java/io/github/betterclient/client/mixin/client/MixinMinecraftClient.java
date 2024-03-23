package io.github.betterclient.client.mixin.client;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.MinecraftAccess;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import io.github.betterclient.client.mod.impl.other.BedrockBridgeMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftAccess {
    @Shadow
    private static int currentFps;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Override
    public int getFPS() {
        return currentFps;
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    public void attackSwing(CallbackInfo ci) {
        BallSack.getInstance().bus.call(new HitEntityEvent(this.player, ((EntityHitResult) this.crosshairTarget).getEntity()));
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack onItemUse(ClientPlayerEntity player, Hand hand) {
        ItemStack itemStack = this.player.getStackInHand(hand);

        if (BedrockBridgeMod.isEnabled())
            BedrockBridgeMod.get().checkReachAroundAndExecute(hand, itemStack);

        return itemStack;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
    public MinecraftClient red(MinecraftClient client) {
        new BallSack();

        return client;
    }
}
