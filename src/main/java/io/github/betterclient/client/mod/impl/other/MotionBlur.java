package io.github.betterclient.client.mod.impl.other;

import com.google.gson.JsonSyntaxException;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.ShaderEffectAccessor;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.impl.modutil.MotionBlurShader;
import io.github.betterclient.client.mod.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.Uniform;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class MotionBlur extends Module {
    public HashMap<Identifier, Resource> resources = new HashMap<>();
    public NumberSetting blurStrength = new NumberSetting("Blur Strength", 50, 1, 99);

    public Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");
    public ShaderEffect shader;
    public float currentBlur;

    public int lastWidth;
    public int lastHeight;

    public MotionBlur() {
        super("Motion Blur", Category.OTHER);
        this.addSetting(blurStrength);

        this.resources.put(shaderLocation, new MotionBlurShader());
    }

    public static MotionBlur get() {
        return (MotionBlur) BallSack.getInstance().moduleManager.getModuleByName("Motion Blur");
    }

    public void onUpdate() {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((shader == null || client.getWindow().getWidth() != lastWidth
                || client.getWindow().getHeight() != lastHeight)
                && client.getWindow().getWidth() > 0
                && client.getWindow().getHeight() > 0) {
            currentBlur = getBlur();
            try {
                shader = new ShaderEffect(client.getTextureManager(), client.getResourceManager(),
                        client.getFramebuffer(), shaderLocation);
                shader.setupDimensions(MinecraftClient.getInstance().getWindow().getWidth(),
                        MinecraftClient.getInstance().getWindow().getHeight());
            } catch (Exception e) {e.printStackTrace();}
        }
        if (currentBlur != getBlur() && shader != null) {
            ((ShaderEffectAccessor) shader).getPasses().forEach(shader -> {
                Uniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if (blendFactor != null) {
                    blendFactor.set(getBlur());
                }
            });
            currentBlur = getBlur();
        }

        lastWidth = MinecraftClient.getInstance().getWindow().getWidth();
        lastHeight = MinecraftClient.getInstance().getWindow().getHeight();
    }

    private float getBlur() {
        return this.blurStrength.value / 100F;
    }
}
