package io.github.betterclient.client.mixin;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.MinecraftAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements MinecraftAccess {
    @Shadow private static int currentFps;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void meWhenThe(RunArgs args, CallbackInfo ci) {
        new BallSack();
    }

    @Override
    public int getFPS() {
        return currentFps;
    }
}
