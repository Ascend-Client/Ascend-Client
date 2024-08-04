package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.ModeSetting;

public class SuperSecretSettings extends Module {
    public ModeSetting mode = new ModeSetting("Mode",
            "NOTCH", "NOTCH", "FXAA", "ART", "BUMPY",
            "BLOBS2", "PENCIL", "COLOR_CONVOLVE", "DECONVERGE", "FLIP",
            "INVERT", "NTSC", "OUTLINE", "PHOSPHOR", "SCAN_PINCUSHION",
            "SOBEL", "BITS", "DESATURATE", "GREEN", "BLUR", "WOBBLE",
            "BLOBS", "ANTIALIAS", "CREEPER", "SPIDER");
    public ShaderEffect shader;
    public int lastWidth, lastHeight;
    public String oldMode = "NOTCH";

    public SuperSecretSettings() {
        super("Super Secret", Category.OTHER, null);
        this.addSetting(mode);
    }

    @Override
    public void toggle() {
        if(MotionBlur.get().toggled)
            MotionBlur.get().toggle();

        super.toggle();
    }

    public static SuperSecretSettings get() {
        return (SuperSecretSettings) BallSack.getInstance().moduleManager.getModuleByName("Super Secret");
    }

    public void onUpdate() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (((client.getWindow().width() != lastWidth || client.getWindow().height() != lastHeight) && client.getWindow().width() > 0 && client.getWindow().height() > 0) || !this.oldMode.equals(this.mode.value)) {
            if(client.getWindow().isNotFocused() || client.getWindow().width() <= 0 || client.getWindow().height() <= 0) return;
            if(shader != null)
                shader.close();

            shader = IBridge.newShaderEffect(new IBridge.Identifier("shaders/post/" + this.mode.value.toLowerCase() + ".json"));

            shader.setupDimensions(client.getWindow().width(), client.getWindow().height());
        }

        lastWidth = client.getWindow().width();
        lastHeight = client.getWindow().height();
    }
}
