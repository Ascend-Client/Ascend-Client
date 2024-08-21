package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.ColorSetting;
import io.github.betterclient.client.util.FileResource;

import java.awt.*;

public class CrosshairMod extends Module {
    public ColorSetting mainColor = new ColorSetting("Color", Color.WHITE);
    public ColorSetting hitColor = new ColorSetting("Target Color", Color.RED);
    public BooleanSetting texture = new BooleanSetting("Target Texture", true);
    private final IBridge.Identifier ID;

    public CrosshairMod() {
        super("Crosshair", Category.OTHER, null);
        this.addSetting(mainColor);
        this.addSetting(hitColor);
        this.addSetting(texture);
        ID = new IBridge.Identifier("minecraft:textures/ballsack/CrosshairModTexture.png");
        BallSack.getInstance().resources.put(ID, new FileResource("/assets/ballsack/CrossModTexture.png"));
    }

    public boolean render() {
        Color clr = hitColor.getColor();

        IBridge.MatrixStack matrices = IBridge.newMatrixStack();
        IBridge.InternalBridge ibridge = IBridge.internal();
        IBridge.MinecraftClient client = IBridge.getInstance().getClient();
        IBridge.Window window = client.getWindow();

        client.setShaderColor(clr.getRed() / 255f, clr.getGreen() / 255f, clr.getBlue() / 255f, clr.getAlpha() / 255f);

        if(!texture.value) return false;
        client.setShaderTexture(0, ID);
        ibridge.drawTexture(matrices, window.scaledWidth() / 2 - 5, window.scaledHeight() / 2 - 5, 0, 0, 256, 256, 256, 256);
        client.emptyShaderColor();
        return true;
    }
}
