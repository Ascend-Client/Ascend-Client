package net.fabricmc.fabric.api.mixins;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("HEAD"), method = "tick")
    private void onStartTick(CallbackInfo ci) {
        for (ClientTickEvents.StartTick registrar : ClientTickEvents.START_CLIENT_TICK.registrars) {
           registrar.onStartTick((MinecraftClient) (Object) this);
        }
    }
}
