package io.github.betterclient.client.ui.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static net.minecraft.client.gui.DrawableHelper.fill;

import static io.github.betterclient.client.util.UIUtil.*;

public class CustomLoadingOverlay {
    public static boolean isFirst = true;
    public static float lastPhaseStartProgress = 0;
    public static int circleY, circleRadius = 10;

    public static int circleColor = new Color(255, 255, 255, 120).getRGB();
    public static boolean isDoingAnimation = false;
    public static boolean doRender = true;
    public static boolean isPackReload = false;

    public static void init() {
        isPackReload = false;
    }

    public static void render(MatrixStack stack, float progress) {
        if(progress < 0.7) doRender = true;
        if(!doRender) {
            isPackReload = true;
            if(MinecraftClient.getInstance().currentScreen != null)
                MinecraftClient.getInstance().currentScreen.render(stack, 0, 0, 0); //it's our screen anyway who cares
            return;
        }
        isDoingAnimation = false;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        fill(stack, 0, 0, width, height, Color.black.getRGB());

        if(progress < 0.9 || isPackReload) {
            client.getTextureManager().bindTexture(new Identifier("textures/ballsack/load.png"));
            RenderSystem.enableBlend();
            RenderSystem.color4f(1, 1, 1, 1);
            drawTexture(stack, 0, 0, 0, 0, width, height, width, height);
            RenderSystem.disableBlend();

            renderOutline(stack, 35, (int) (height * 0.73), width - 35, (int) (height * 0.87), Color.RED.getRGB());
            drawRoundedRect(40, height * 0.75 , map(progress, 0, 1, 0, width - 40), height * 0.85, 2f, -1);
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
            renderCircle(width * 0.5, circleY, circleRadius, circleColor);
        }
    }
}
