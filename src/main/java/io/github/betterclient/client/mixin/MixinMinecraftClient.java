package io.github.betterclient.client.mixin;

import io.github.betterclient.client.BallSack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void meWhenThe(RunArgs args, CallbackInfo ci) {
        new BallSack();
    }
}
