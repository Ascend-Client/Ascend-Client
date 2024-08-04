package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.impl.modutil.MotionBlurShader;
import io.github.betterclient.client.mod.setting.NumberSetting;
import io.github.betterclient.client.util.FileResource;

public class MotionBlur extends Module {
    public NumberSetting blurStrength = new NumberSetting("Blur Strength", 50, 1, 99);

    public Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");
    public ShaderEffect shader;
    public float currentBlur;

    public int lastWidth;
    public int lastHeight;

    public MotionBlur() {
        super("Motion Blur", Category.OTHER, null);
        this.addSetting(blurStrength);

        BallSack.getInstance().resources.put(shaderLocation, new MotionBlurShader());
        BallSack.getInstance().resources.put(new Identifier("minecraft:shaders/program/motion_blur.json"), new FileResource("/assets/minecraft/shaders/program/motion_blur.json"));
        BallSack.getInstance().resources.put(new Identifier("minecraft:shaders/program/motion_blur.fsh"), new FileResource("/assets/minecraft/shaders/program/motion_blur.fsh"));
    }

    public static MotionBlur get() {
        return (MotionBlur) BallSack.getInstance().moduleManager.getModuleByName("Motion Blur");
    }

    @Override
    public void toggle() {
        if(SuperSecretSettings.get().toggled)
            SuperSecretSettings.get().toggle();
        super.toggle();
    }

    public void onUpdate() {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((shader == null || client.getWindow().width() != lastWidth
                || client.getWindow().height() != lastHeight)
                && client.getWindow().width() > 0
                && client.getWindow().height() > 0) {
            if(client.getWindow().isNotFocused() || client.getWindow().width() <= 0 || client.getWindow().height() <= 0) return;
            currentBlur = getBlur();
            if(shader != null)
                shader.close();

            shader = IBridge.newShaderEffect(shaderLocation);

            shader.setupDimensions(client.getWindow().width(),
                    client.getWindow().height());
        }
        if (currentBlur != getBlur() && shader != null) {
            shader.getPasses().forEach(shader -> shader.setUniformByName("BlendFactor", getBlur()));
            currentBlur = getBlur();
        }

        lastWidth = client.getWindow().width();
        lastHeight = client.getWindow().height();
    }

    public float getBlur() {
        return this.blurStrength.value / 100F;
    }
}
