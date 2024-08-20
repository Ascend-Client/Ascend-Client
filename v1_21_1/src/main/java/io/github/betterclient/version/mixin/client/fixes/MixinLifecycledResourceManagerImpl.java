package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mixin(LifecycledResourceManagerImpl.class)
public class MixinLifecycledResourceManagerImpl {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void why(Identifier id, CallbackInfoReturnable<Optional<Resource>> cir) {
        BallSack.getInstance().resources.forEach((identifier, resource) -> {
            if(identifier.pointer.toString().equals(id.toString())) {
                cir.setReturnValue(Optional.of(new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), resource.resourceSupplier::getInputStream)));
            }
        });
    }
}
