package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.MotionBlur;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void hehehehehehehehehehehhehehehehhehehhehehehhehe(Identifier id, CallbackInfoReturnable<Resource> cir) {
        if (MotionBlur.get().resources.get(id) != null) {
            cir.setReturnValue(MotionBlur.get().resources.get(id));
        }
    }
}
