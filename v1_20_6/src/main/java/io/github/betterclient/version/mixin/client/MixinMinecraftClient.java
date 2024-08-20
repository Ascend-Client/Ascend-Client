package io.github.betterclient.version.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.impl.other.SuperSecretSettings;
import io.github.betterclient.client.util.FileResource;
import io.github.betterclient.version.Version;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import io.github.betterclient.fabric.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.tutorial.TutorialManager;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    public void attackSwing(CallbackInfoReturnable<Boolean> cir) {
        BallSack.getInstance().bus.call(new HitEntityEvent((IBridge.PlayerEntity) this.player, (IBridge.Entity) ((EntityHitResult) this.crosshairTarget).getEntity()));
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
    public MinecraftClient red(MinecraftClient client) {
        new BallSack();

        for (String value : SuperSecretSettings.get().mode.values) {
            BallSack.getInstance().resources.put(new IBridge.Identifier("shaders/post/" + value.toLowerCase() + ".json"), new FileResource("/assets/minecraft/shaders/post/" + value.toLowerCase() + ".json"));
        }

        //I will un-hardcode this later frfr
        for(String value : List.of("antialias.fsh", "antialias.json", "bits_fix.json", "blobs.fsh", "blobs.json", "blobs.vsh", "blobs2.fsh", "blobs2.json", "blur.json", "brightness_threshold.fsh", "brightness_threshold.json", "bumpy.fsh", "bumpy.json", "bumpy.vsh", "deconverge.fsh", "deconverge.json", "downscale.fsh", "downscale.json", "downscale.vsh", "flip.json", "flip.vsh", "fxaa.fsh", "fxaa.json", "fxaa.vsh", "merge_bloom.fsh", "merge_bloom.json", "notch.fsh", "notch.json", "ntsc_decode.fsh", "ntsc_decode.json", "ntsc_encode.fsh", "ntsc_encode.json", "outline.fsh", "outline.json", "outline_combine.fsh", "outline_combine.json", "outline_soft.fsh", "outline_soft.json", "outline_watercolor.fsh", "outline_watercolor.json", "overlay.fsh", "overlay.json", "phosphor.fsh", "phosphor.json", "scan_pincushion.fsh", "scan_pincushion.json", "shareware.fsh", "shareware.json", "sobel.fsh", "sobel.json", "wobble.fsh", "wobble.json")) {
            BallSack.getInstance().resources.put(new IBridge.Identifier("shaders/program/" + value), new FileResource("/assets/minecraft/shaders/program/" + value));
        }

        return client;
    }

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/option/GameOptions;)Lnet/minecraft/client/tutorial/TutorialManager;"))
    public TutorialManager hi(MinecraftClient minecraftClient, GameOptions gameOptions, Operation<TutorialManager> original) {
        try {
            FabricLoader.getInstance().callClientMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return original.call(minecraftClient, gameOptions);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/PeriodicNotificationManager;<init>(Lnet/minecraft/util/Identifier;Lit/unimi/dsi/fastutil/objects/Object2BooleanFunction;)V"))
    public void initBridge(RunArgs args, CallbackInfo ci) {
        try {
            Version.setup();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
