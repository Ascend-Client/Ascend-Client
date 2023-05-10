package io.github.betterclient.client.mixin;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.MinecraftAccess;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements MinecraftAccess {
    @Shadow private static int currentFps;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void meWhenThe(RunArgs args, CallbackInfo ci) {
        new BallSack();
    }

    @Override
    public int getFPS() {
        return currentFps;
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    public void attackSwing(CallbackInfo ci) {
        BallSack.getInstance().bus.call(new HitEntityEvent(this.player, ((EntityHitResult) this.crosshairTarget).getEntity()));
    }
}
