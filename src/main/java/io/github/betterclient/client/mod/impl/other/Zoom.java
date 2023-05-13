package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.KeyBindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
    public boolean isZooming = false;
    public static boolean isZoomed = false;
    public static double zoomFactor = 0.25D;

    private static double cachedFov;
    private static boolean cachedSmoothCamera;

    public KeyBindSetting bind = new KeyBindSetting("Zoom Key", GLFW.GLFW_KEY_C, () -> {
        isZooming = true;
    }, () -> {
        isZooming = false;
    });

    public Zoom() {
        super("Zoom", Category.OTHER);
        this.addSetting(this.bind);
    }

    public static Zoom get() {
        return (Zoom) BallSack.getInstance().moduleManager.getModuleByName("Zoom");
    }

    public double handleZoom(double fov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), bind.key)) {

            if (!isZoomed) {
                cachedSmoothCamera = mc.options.smoothCameraEnabled;
            }
            mc.options.smoothCameraEnabled = true;
            isZoomed = true;

            double modifiedZoom = fov * zoomFactor;
            if (modifiedZoom < 1.0D) {
                modifiedZoom = 1.0D;
            }
            if (modifiedZoom > 170.0D) {
                modifiedZoom = 170.0D;
            }
            cachedFov = modifiedZoom;
            return (modifiedZoom);

        } else {

            if (isZoomed) {
                mc.options.smoothCameraEnabled = cachedSmoothCamera;
                isZoomed = false;
                zoomFactor = 0.25D;
            }

        }

        cachedFov = fov;

        return fov;

    }
}
