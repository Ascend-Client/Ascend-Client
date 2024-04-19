package io.github.betterclient.version.mixin.client;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.MouseEvent;
import io.github.betterclient.client.event.impl.MouseScrollEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", shift = At.Shift.BEFORE))
    public void beforeSet(long window, int button, int action, int mods, CallbackInfo ci) {
        BallSack.getInstance().bus.call(new MouseEvent(button, action == 1));
    }

    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    public void handleMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        double d = (this.client.options.getDiscreteMouseScroll().getValue() ? Math.signum(vertical) : vertical) * this.client.options.getMouseWheelSensitivity().getValue();
        MouseScrollEvent event = new MouseScrollEvent(d);
        BallSack.getInstance().bus.call(event);
        if(event.cancelled) ci.cancel();
    }
}
