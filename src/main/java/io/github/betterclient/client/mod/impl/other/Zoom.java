package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.MouseScrollEvent;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.KeyBindSetting;
import io.github.betterclient.client.mod.setting.NumberSetting;

public class Zoom extends Module {
    public boolean isZooming = false;
    public static boolean isZoomed = false;
    public static double zoomFactor = 0.25D;

    private static boolean cachedSmoothCamera;

    public KeyBindSetting bind = new KeyBindSetting("Zoom Key", IBridge.getKeys().KEY_C, () -> isZooming = true, () -> isZooming = false);
    public BooleanSetting flip = new BooleanSetting("Flip Scroll", false);
    public NumberSetting zoomFactorStart = new NumberSetting("Start zoom", 25, 10, 70);

    public Zoom() {
        super("Zoom", Category.OTHER, null);
        this.addSetting(this.bind);
        this.addSetting(this.flip);
        this.addSetting(this.zoomFactorStart);
    }

    public static Zoom get() {
        return (Zoom) Ascend.getInstance().moduleManager.getModuleByName("Zoom");
    }

    @Override
    public void onEnabled() {
        zoomFactor = zoomFactorStart.value / 100D;
    }

    public double handleZoom(double fov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentScreenPointer() != null)
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
                zoomFactor = this.zoomFactorStart.value / 100D;
            }

        }

        return fov;

    }

    @EventTarget
    public void handleScrollWheel(MouseScrollEvent event) {
        if(!isZooming) return;

        if (event.amount == (this.flip.value ? -1 : 1) && zoomFactor > 0.1) {
            zoomFactor *= 0.9;
        } else if (event.amount == (this.flip.value ? 1 : -1) && zoomFactor < 0.7) {
            zoomFactor *= 1.1;
        }

        event.cancel();
    }
}
