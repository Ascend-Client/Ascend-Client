package io.github.betterclient.client.mod.impl.other;

import com.google.gson.JsonSyntaxException;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.PostEffectProcessorAccessor;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.impl.modutil.MotionBlurShader;
import io.github.betterclient.client.mod.setting.NumberSetting;
import io.github.betterclient.client.util.FileResource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.HashMap;

public class MotionBlur extends Module {
    public NumberSetting blurStrength = new NumberSetting("Blur Strength", 50, 1, 99);

    public Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");
    public PostEffectProcessor shader;
    public float currentBlur;

    public int lastWidth;
    public int lastHeight;

    public MotionBlur() {
        super("Motion Blur", Category.OTHER);
        this.addSetting(blurStrength);

        BallSack.getInstance().resources.put(shaderLocation, new MotionBlurShader());
        BallSack.getInstance().resources.put(new Identifier("minecraft:shaders/program/motion_blur.json"), new FileResource("/assets/minecraft/shaders/program/motion_blur.json"));
        BallSack.getInstance().resources.put(new Identifier("minecraft:shaders/program/motion_blur.fsh"), new FileResource("/assets/minecraft/shaders/program/motion_blur.fsh"));
    }

    public static MotionBlur get() {
        return (MotionBlur) BallSack.getInstance().moduleManager.getModuleByName("Motion Blur");
    }

    public void onUpdate() {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((shader == null || MinecraftClient.getInstance().getWindow().getWidth() != lastWidth
                || MinecraftClient.getInstance().getWindow().getHeight() != lastHeight)
                && MinecraftClient.getInstance().getWindow().getWidth() > 0
                && MinecraftClient.getInstance().getWindow().getHeight() > 0) {
            currentBlur = getBlur();
            try {
                shader = new PostEffectProcessor(client.getTextureManager(), client.getResourceManager(),
                        client.getFramebuffer(), shaderLocation);
                shader.setupDimensions(MinecraftClient.getInstance().getWindow().getWidth(),
                        MinecraftClient.getInstance().getWindow().getHeight());
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
        if (currentBlur != getBlur() && shader != null) {
            ((PostEffectProcessorAccessor) shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if (blendFactor != null) {
                    blendFactor.set(getBlur());
                }
            });
            currentBlur = getBlur();
        }

        lastWidth = MinecraftClient.getInstance().getWindow().getWidth();
        lastHeight = MinecraftClient.getInstance().getWindow().getHeight();
    }

    public float getBlur() {
        return this.blurStrength.value / 100F;
    }
}
