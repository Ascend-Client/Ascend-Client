package io.github.betterclient.client.mixin.client;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.MouseEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", shift = At.Shift.BEFORE))
    public void beforeSet(long window, int button, int action, int mods, CallbackInfo ci) {
        BallSack.getInstance().bus.call(new MouseEvent(button, action == 1));
    }
}
