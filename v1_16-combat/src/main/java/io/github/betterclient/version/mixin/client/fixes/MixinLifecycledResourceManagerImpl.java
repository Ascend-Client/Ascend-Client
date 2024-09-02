package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.version.util.ResourceImplementation;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinLifecycledResourceManagerImpl {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void why(Identifier id, CallbackInfoReturnable<Resource> cir) {
        Ascend.getInstance().resources.forEach((identifier, resource) -> {
            if(identifier.pointer.toString().equals(id.toString())) {
                cir.setReturnValue(new ResourceImplementation(resource.resourceSupplier));
            }
        });
    }

    @Inject(method = "getResource", at = @At(value = "NEW", args = "class=java/io/FileNotFoundException"), cancellable = true)
    public void hi(Identifier id, CallbackInfoReturnable<Resource> cir) {
        IBridge.Identifier identifier = new IBridge.Identifier(id);
        IBridge.Resource resource = Ascend.getInstance().findLoadedResource(identifier);

        if(resource != null) {
            cir.setReturnValue(new ResourceImplementation(resource.resourceSupplier));
            cir.cancel();
        }
    }
}
