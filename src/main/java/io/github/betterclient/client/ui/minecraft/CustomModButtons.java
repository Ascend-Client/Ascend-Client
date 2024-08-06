package io.github.betterclient.client.ui.minecraft;

import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.util.UIUtil;
import io.github.betterclient.fabric.relocate.loader.api.FabricLoader;

import java.awt.*;
import java.lang.reflect.Field;

import static io.github.betterclient.client.util.UIUtil.*;

public class CustomModButtons {
    static int bgcolor = new Color(0, 0, 0, 120).getRGB();
    static int lastMouseX, lastMouseY;

    static void render(MatrixStack matrices) {
        TextRenderer textRenderer = MinecraftClient.getInstance().getTextRenderer();

        int index = 0;
        float[] iPos;

        if(FabricLoader.getInstance().isModLoaded("replaymod")) {
            drawRoundedRect(25, 25, 125, 45, 2f, bgcolor);
            iPos = getIdealRenderingPosForText("Replays", 25, 25, 125, 45);
            textRenderer.draw(matrices, "Replays", iPos[0], iPos[1], -1);

            index++;
        }


        if(FabricLoader.getInstance().isModLoaded("mod menu")) {
            drawRoundedRect(25, 25 + (index * 30), 125, 45 + (index * 30), 2f, bgcolor);
            iPos = getIdealRenderingPosForText("Mod Menu", 25, 25 + (index * 30), 125, 45 + (index * 30));
            textRenderer.draw(matrices, "Mod Menu", iPos[0], iPos[1], -1);
            index++;
        }


        /*
        Other mods
        drawRoundedRect(25, 25 + (index * 30), 125, 45 + (index * 30), 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Other Mod", 25, 25 + (index * 30), 125, 45 + (index * 30));
        textRenderer.draw(matrices, "Other mod", iPos[0], iPos[1], -1);
        index++;*/

    }

    static void mouseClicked(double mouseX, double mouseY) {
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        int index = 0;

        if(FabricLoader.getInstance().isModLoaded("replaymod")) {
            if(isMouseOn(25, 25, 125, 45)) {
                try {
                    Class<?> replayModReplayClass = Class.forName("com.replaymod.replay.ReplayModReplay");
                    Object replayModInstance = replayModReplayClass.getDeclaredField("instance").get(null);
                    Class<?> replayViewerScreenClass = Class.forName("com.replaymod.replay.gui.screen.GuiReplayViewer");
                    Object replayViewer = replayViewerScreenClass.getConstructor(replayModReplayClass).newInstance(replayModInstance);
                    Class<?> abstractGuiScreenClass = Class.forName("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen");
                    Field wrappedField = abstractGuiScreenClass.getDeclaredField("wrapped");
                    wrappedField.setAccessible(true);
                    MinecraftClient.getInstance().openNonCustomScreen(wrappedField.get(replayViewer));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            index++;
        }

        if(isMouseOn(25, 25 + (index * 30), 125, 45 + (index * 30)) && FabricLoader.instance.isModLoaded("Mod Menu")) {
            try {
                Class<?> cls = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
                Object obj = cls.getConstructor(Class.forName("net.minecraft.client.gui.screen.Screen")).newInstance(MinecraftClient.getInstance().getCurrentScreenPointer());
                MinecraftClient.getInstance().openNonCustomScreen(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static boolean isMouseOn(double x, double y, double endX, double endY) {
        return UIUtil.basicCollisionCheck(
                lastMouseX, lastMouseY,
                x, y,
                endX, endY
        );
    }
}
