package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.Zoom;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("RETURN"), method = "getFov", cancellable = true)
    public void onGetFOVModifier(CallbackInfoReturnable<Double> info) {

        if(!Zoom.get().toggled)
            return;

        double defaultFOV = info.getReturnValue();
        info.setReturnValue(Zoom.get().handleZoom(defaultFOV));

    }

}