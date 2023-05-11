package io.github.betterclient.client.mixin;

import io.github.betterclient.client.access.CameraControl;
import io.github.betterclient.client.mod.impl.other.FreeLook;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity implements CameraControl {
    private float cameraPitch;

    private float cameraYaw;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void changeCameraLookDirection(double xDelta, double yDelta, CallbackInfo ci) {
        if (!FreeLook.get().perspectiveToggled || !((Object) this instanceof ClientPlayerEntity)) return;

        double pitchDelta = (yDelta * 0.15);
        double yawDelta = (xDelta * 0.15);

        this.cameraPitch = MathHelper.clamp(this.cameraPitch + (float) pitchDelta, -90.0f, 90.0f);
        this.cameraYaw += (float) yawDelta;

        ci.cancel();
    }

    @Override
    public float getCameraPitch() {
        return this.cameraPitch;
    }

    @Override
    public float getCameraYaw() {
        return this.cameraYaw;
    }

    @Override
    public void setCameraPitch(float pitch) {
        this.cameraPitch = pitch;
    }

    @Override
    public void setCameraYaw(float yaw) {
        this.cameraYaw = yaw;
    }
}
