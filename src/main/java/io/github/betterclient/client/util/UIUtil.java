package io.github.betterclient.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class UIUtil {
    private static int stX = 0,stY = 0;

    public static void setStart(int stX, int stY) {
        UIUtil.stX = stX;
        UIUtil.stY = stY;
    }

    /**
     * @param x x pos
     * @param y y pos
     * @param endX ending x (x + uiwidth)
     * @param endY ending y (y + uiheight)
     * @param radius radius of round
     * @param color rect color
     */
    public static void drawRoundedRect(double x, double y, double endX, double endY, double radius, int color) {
        x+=stX;
        y+=stY;
        endX+=stX;
        endY+=stY;

        double store = x;
        x = Math.min(x, endX);
        endX = Math.max(store, endX);

        store = y;
        y = Math.min(y, endY);
        endY = Math.max(store, endY);

        radius/=2;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        int n2;

        Matrix4f mat = new MatrixStack().peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        for (n2 = 0; n2 <= 90; n2 += 3) {
            builder.vertex(mat, (float) (x + radius + Math.sin((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), (float) (y + radius + Math.cos((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), 0).color(color).next();
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            builder.vertex(mat, (float) (x + radius + Math.sin((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), (float) (endY - radius + Math.cos((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), 0).color(color).next();
        }
        for (n2 = 0; n2 <= 90; n2 += 3) {
            builder.vertex(mat, (float) (endX - radius + Math.sin((double) n2 * Math.PI / (double) 180) * radius), (float) (endY - radius + Math.cos((double) n2 * Math.PI / (double) 180) * radius), 0).color(color).next();
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            builder.vertex(mat, (float) (endX - radius + Math.sin((double) n2 * Math.PI / (double) 180) * radius), (float) (y + radius + Math.cos((double) n2 * Math.PI / (double) 180) * radius), 0).color(color).next();
        }

        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.disableBlend();
    }

    public static void renderOutline(MatrixStack i, int x, int y, int endingX, int endingY, int color) {
        DrawableHelper.fill(i, x, y, endingX, y + 1, color);
        DrawableHelper.fill(i, x, endingY, endingX, endingY + 1, color);
        DrawableHelper.fill(i, x, y, x + 1, endingY, color);
        DrawableHelper.fill(i, endingX, y, endingX + 1, endingY, color);
    }

    public static void renderCircle(double x, double y, float halfRadius, int color) {
        drawRoundedRect(x - halfRadius, y - halfRadius, x + halfRadius, y + halfRadius, halfRadius * 2, color);
    }

    public static boolean basicCollisionCheck(double mouseX, double mouseY, double x, double y, double endX, double endY) {
        return mouseX >= x & mouseX <= endX & mouseY >= y & mouseY <= endY;
    }

    public static int[] getIdealRenderingPosForText(String text, double x, double y, double endX, double endY) {
        return getIdealRenderingPosForText(text, x, y, endX, endY, 1f);
    }

    /**
        found this in arduino source code
        the value x is between in_min and in_max
        and the value gets on out_min and out_max
        idk how to tell you this
     */
    public static double map(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static int[] getIdealRenderingPosForText(String text, double x, double y, double endX, double endY, float scale) {
        int[] renderingPos = new int[2];

        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraft.textRenderer;
        int textWidth = textRenderer.getWidth(text);
        int textHeight = textRenderer.fontHeight;

        // Adjust the rendering position based on the specified parameters and scale
        renderingPos[0] = (int) ((x + endX) / 2 - textWidth / 2 * scale);
        renderingPos[1] = (int) ((y + endY) / 2 - textHeight / 2 * scale);

        return renderingPos;
    }

    public static void enableScissor(float x, float y, float endX, float endY) {
        var width = endX - x;
        var height = endY - y;

        glEnable(GL_SCISSOR_TEST);
        var res = MinecraftClient.getInstance().getWindow();
        x = (float) (x * res.getScaleFactor());
        height = (float) (height * res.getScaleFactor());
        y = (float) (res.getHeight() - (y * res.getScaleFactor()) - height);
        width = (float) (width * res.getScaleFactor());
        glScissor((int) x, (int) y, (int) width, (int) height);
    }

    public static void disableScissor() {
        glDisable(GL_SCISSOR_TEST);
    }
}
