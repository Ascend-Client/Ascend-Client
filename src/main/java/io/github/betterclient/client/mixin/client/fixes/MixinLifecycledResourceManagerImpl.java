package io.github.betterclient.client.mixin.client.fixes;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.impl.other.MotionBlur;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LifecycledResourceManagerImpl.class)
public class MixinLifecycledResourceManagerImpl {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void why(Identifier id, CallbackInfoReturnable<Optional<Resource>> cir) {
        BallSack.getInstance().resources.forEach((identifier, resource) -> {
            if(identifier.toString().equals(id.toString())) {
                cir.setReturnValue(Optional.of(resource));
            }
        });
    }
}
