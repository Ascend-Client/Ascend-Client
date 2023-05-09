package io.github.betterclient.client.mixin;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void event(PlayerEntity player, Entity target, CallbackInfo ci) {
        BallSack.getInstance().bus.call(new HitEntityEvent(player, target));
    }
}
