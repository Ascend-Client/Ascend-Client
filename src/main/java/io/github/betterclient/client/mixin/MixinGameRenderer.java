package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.NoHurtCam;
import io.github.betterclient.client.mod.impl.other.Zoom;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
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

    @ModifyConstant(method = "bobViewWhenHurt", constant = @Constant(floatValue = 14.0F))
    public float changeArg(float fourTeen) {
        if(NoHurtCam.get().isToggled()) {
            return 14 * (NoHurtCam.get().setting.value / 100F);
        } else {
            return fourTeen;
        }
    }
}