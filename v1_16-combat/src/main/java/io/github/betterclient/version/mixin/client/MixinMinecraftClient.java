package io.github.betterclient.version.mixin.client;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.version.Version;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.version.mods.BedrockBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.DummyProfiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    public void attackSwing(CallbackInfo ci) {
        BallSack.getInstance().bus.call(new HitEntityEvent((IBridge.PlayerEntity) this.player, (IBridge.Entity) ((EntityHitResult) this.crosshairTarget).getEntity()));
    }

    @Inject(method = "disconnect()V", at = @At("HEAD"))
    public void enableServer(CallbackInfo ci) {
        BedrockBridge.get().setServerAllowing(true);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
    public MinecraftClient red(MinecraftClient client) {
        new BallSack();

        return client;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;<init>(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;)V"))
    public MinecraftClient hi(MinecraftClient client) {
        try {
            FabricLoader.getInstance().callClientMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/profiler/DummyProfiler;INSTANCE:Lnet/minecraft/util/profiler/DummyProfiler;"))
    public DummyProfiler initBridge() {
        try {
            Version.setup();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return DummyProfiler.INSTANCE;
    }
}
