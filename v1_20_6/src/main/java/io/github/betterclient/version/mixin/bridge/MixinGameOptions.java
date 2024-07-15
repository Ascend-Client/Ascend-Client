package io.github.betterclient.version.mixin.bridge;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinGameOptions implements IBridge.GameOptions {
    @Shadow @Final public KeyBinding forwardKey;

    @Shadow @Final public KeyBinding backKey;

    @Shadow @Final public KeyBinding rightKey;

    @Shadow @Final public KeyBinding leftKey;

    @Shadow private Perspective perspective;

    @Shadow @Final private SimpleOption<Double> gamma;

    @Shadow public boolean smoothCameraEnabled;

    @Mutable @Shadow @Final
    public KeyBinding[] allKeys;

    @Override
    public boolean forwardPressed() {
        return this.forwardKey.isPressed();
    }

    @Override
    public boolean backPressed() {
        return this.backKey.isPressed();
    }

    @Override
    public boolean rightPressed() {
        return this.rightKey.isPressed();
    }

    @Override
    public boolean leftPressed() {
        return this.leftKey.isPressed();
    }

    @Override
    public IBridge.Perspective getPerspective() {
        if(this.perspective == Perspective.FIRST_PERSON) {
            return IBridge.Perspective.FIRST_PERSON;
        } else if(this.perspective == Perspective.THIRD_PERSON_BACK) {
            return IBridge.Perspective.THIRD_PERSON_BACK;
        } else {
            return IBridge.Perspective.THIRD_PERSON_FRONT;
        }
    }

    @Override
    public void setPerspective(IBridge.Perspective perspective) {
        if(perspective == IBridge.Perspective.FIRST_PERSON) {
            this.perspective = Perspective.FIRST_PERSON;
        } else if(perspective == IBridge.Perspective.THIRD_PERSON_BACK) {
            this.perspective = Perspective.THIRD_PERSON_BACK;
        } else {
            this.perspective = Perspective.THIRD_PERSON_FRONT;
        }
    }

    @Override
    public double getGamma() {
        return this.gamma.getValue();
    }

    @Override
    public void setGamma(double v) {
        this.gamma.setValue(v);
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
        this.allKeys = ArrayUtils.add(this.allKeys, (KeyBinding) bind.pointer);
    }
}
