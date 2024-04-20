package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.util.ResourceImplementation;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Mixin(NamespaceResourceManager.class)
public class MixinNamespaceResourceManager {
    @Inject(method = "getResource", at = @At(value = "NEW", args = "class=java/io/FileNotFoundException"), cancellable = true)
    public void hi(Identifier id, CallbackInfoReturnable<Resource> cir) {
        IBridge.Identifier identifier = new IBridge.Identifier(id);
        IBridge.Resource resource = BallSack.getInstance().findLoadedResource(identifier);

        if(resource != null) {
            cir.setReturnValue(new ResourceImplementation(resource.resourceSupplier));
            cir.cancel();
        }
    }
}
