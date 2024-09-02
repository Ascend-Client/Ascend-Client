package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceFactory.class)
public interface MixinResourceFactory {
    @Inject(method = "getResourceOrThrow", at = @At("HEAD"), cancellable = true)
    default void impl(Identifier id, CallbackInfoReturnable<Resource> cir) {
        IBridge.Identifier identifier = new IBridge.Identifier(id);
        IBridge.Resource resource = Ascend.getInstance().findLoadedResource(identifier);

        if(resource != null) {
            cir.setReturnValue(new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), resource.resourceSupplier::getInputStream));
            cir.cancel();
        }
    }
}
