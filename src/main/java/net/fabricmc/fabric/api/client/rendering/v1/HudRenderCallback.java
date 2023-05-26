package net.fabricmc.fabric.api.client.rendering.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventImpl;
import net.minecraft.client.util.math.MatrixStack;

public interface HudRenderCallback {
    public static Event<HudRenderCallback> EVENT = new EventImpl<>();

    void onHudRender(MatrixStack matrixStack, float tickDelta);
}
