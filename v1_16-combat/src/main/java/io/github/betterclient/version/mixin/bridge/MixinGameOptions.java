package io.github.betterclient.version.mixin.bridge;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Perspective;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinGameOptions implements IBridge.GameOptions {
    @Shadow @Final public KeyBinding keyForward;

    @Shadow @Final public KeyBinding keyBack;

    @Shadow @Final public KeyBinding keyRight;

    @Shadow @Final public KeyBinding keyLeft;

    @Shadow private Perspective field_26677;

    @Shadow public boolean smoothCameraEnabled;

    @Mutable @Shadow @Final
    public KeyBinding[] keysAll;

    @Shadow public double gamma;

    @Override
    public boolean forwardPressed() {
        return this.keyForward.isPressed();
    }

    @Override
    public boolean backPressed() {
        return this.keyBack.isPressed();
    }

    @Override
    public boolean rightPressed() {
        return this.keyRight.isPressed();
    }

    @Override
    public boolean leftPressed() {
        return this.keyLeft.isPressed();
    }

    @Override
    public IBridge.Perspective getPerspective() {
        if(this.field_26677 == Perspective.FIRST_PERSON) {
            return IBridge.Perspective.FIRST_PERSON;
        } else if(this.field_26677 == Perspective.THIRD_PERSON_BACK) {
            return IBridge.Perspective.THIRD_PERSON_BACK;
        } else {
            return IBridge.Perspective.THIRD_PERSON_FRONT;
        }
    }

    @Override
    public void setPerspective(IBridge.Perspective perspective) {
        if(perspective == IBridge.Perspective.FIRST_PERSON) {
            this.field_26677 = Perspective.FIRST_PERSON;
        } else if(perspective == IBridge.Perspective.THIRD_PERSON_BACK) {
            this.field_26677 = Perspective.THIRD_PERSON_BACK;
        } else {
            this.field_26677 = Perspective.THIRD_PERSON_FRONT;
        }
    }

    @Override
    public double getGamma() {
        return this.gamma;
    }

    @Override
    public void setGamma(double v) {
        this.gamma = v;
    }

    @Override
    public void setSmoothCameraEnabled(boolean smoothCamera) {
        this.smoothCameraEnabled = smoothCamera;
    }

    @Override
    public boolean isSmoothCamera() {
        return this.smoothCameraEnabled;
    }

    @Override
    public void addBind(IBridge.KeyBinding bind) {
        this.keysAll = ArrayUtils.add(this.keysAll, (KeyBinding) bind.pointer);
    }
}
