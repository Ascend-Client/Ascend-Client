package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.KeyBindSetting;

public class Zoom extends Module {
    public boolean isZooming = false;
    public static boolean isZoomed = false;
    public static double zoomFactor = 0.25D;

    private static boolean cachedSmoothCamera;

    public KeyBindSetting bind = new KeyBindSetting("Zoom Key", IBridge.getKeys().KEY_C, () -> isZooming = true, () -> isZooming = false);

    public Zoom() {
        super("Zoom", Category.OTHER);
        this.addSetting(this.bind);
    }

    public static Zoom get() {
        return (Zoom) BallSack.getInstance().moduleManager.getModuleByName("Zoom");
    }

    public double handleZoom(double fov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.getCurrentScreenPointer() != null)
            return fov;

        if (mc.isKeyPressed(bind.key)) {

            if (!isZoomed) {
                cachedSmoothCamera = mc.getOptions().isSmoothCamera();
            }
            mc.getOptions().setSmoothCameraEnabled(true);
            isZoomed = true;

            double modifiedZoom = fov * zoomFactor;
            if (modifiedZoom < 1.0D) {
                modifiedZoom = 1.0D;
            }
            if (modifiedZoom > 170.0D) {
                modifiedZoom = 170.0D;
            }
            return (modifiedZoom);

        } else {

            if (isZoomed) {
                mc.getOptions().setSmoothCameraEnabled(cachedSmoothCamera);
                isZoomed = false;
                zoomFactor = 0.25D;
            }

        }

        return fov;

    }
}
