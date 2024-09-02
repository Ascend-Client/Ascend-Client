package io.github.betterclient.client.ui.minecraft;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.util.FileResource;

import java.awt.*;

import static io.github.betterclient.client.util.UIUtil.*;

public class CustomLoadingOverlay {
    public static boolean isFirst = true;
    public static float lastPhaseStartProgress = 0;
    public static int circleY, circleRadius = 10;

    public static int circleColor = new Color(255, 255, 255, 120).getRGB();
    public static boolean isDoingAnimation = false;
    public static boolean doRender = true;
    public static boolean isPackReload = false;
    public static IBridge.Identifier ASCEND = new Identifier("minecraft:textures/ascend/load.png");

    public static void init() {
        isPackReload = false;
        Ascend.getInstance().resources.put(ASCEND, new FileResource("/assets/ascend/load.png"));
    }

    public static void render(MatrixStack stack, float progress) {
        if(progress < 0.7) doRender = true;
        if(!doRender) {
            isPackReload = true;
            if(MinecraftClient.getInstance().getCurrentScreenPointer() != null)
                MinecraftClient.getInstance().renderCurrentScreen(stack, MinecraftClient.getInstance().getMouse().getX(), MinecraftClient.getInstance().getMouse().getY(), 0);
            return;
        }
        isDoingAnimation = false;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().scaledWidth();
        int height = client.getWindow().scaledHeight();

        fill(stack, width, height, Color.black.getRGB());

        if(progress < 0.9 || isPackReload) {
            client.setShaderTexture(0, ASCEND);
            client.setShaderColor(1, 1, 1, 1);
            drawTexture(stack, width, height, width, height);

            renderOutline(stack, 35, (int) (height * 0.73), width - 35, (int) (height * 0.87), Color.RED.getRGB());
            drawRoundedRect(40, height * 0.75 , map(progress, 0, 1, 0, width - 40), height * 0.85, 2f, -1, IBridge.newMatrixStack());
        }

        if(progress < 0.8) {
            lastPhaseStartProgress = 0;
            circleRadius = 10;
        }

        if(isPackReload) return;

        if(isFirst && progress >= 0.8) {
            if(lastPhaseStartProgress == 0) lastPhaseStartProgress = progress;

            if(progress < 0.85) {
                //circle falls down (very emotional)
                circleY = (int) map(progress, lastPhaseStartProgress, 0.85, 0, height * 0.5);
                circleRadius = 10;
            } else {
                //circle get bigger (even more emotional)
                circleY = (int) (height * 0.5);
                circleRadius = (int) map(progress, 0.84, 1, 10, (Math.max(width, height) / 2) + 100);
                isDoingAnimation = true;
            }
            renderCircle(width * 0.5, circleY, circleRadius, circleColor, IBridge.newMatrixStack());
        }
    }

    static void fill(MatrixStack stack, int width, int height, int color) {
        IBridge.internal().screen_fill(stack, 0, 0, width, height, color);
    }

    static void drawTexture(MatrixStack matrices, int width, int height, int textureWidth, int textureHeight) {
        IBridge.internal().drawTexture(matrices, 0, 0, 0, 0, width, height, textureWidth, textureHeight);
    }
}
