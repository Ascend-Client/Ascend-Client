package io.github.betterclient.client.util;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;

import java.util.ArrayList;
import java.util.List;

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

        BufferBuilder builder = MinecraftClient.getInstance().getBufferBuilder();

        int n2;

        MatrixStack mat = IBridge.newMatrixStack();

        builder.begin(BeginMode.TRIANGLE_FAN);

        for (n2 = 0; n2 <= 90; n2 += 3) {
            builder.vertex(mat, (float) (x + radius + Math.sin((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), (float) (y + radius + Math.cos((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), 0, color);
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            builder.vertex(mat, (float) (x + radius + Math.sin((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), (float) (endY - radius + Math.cos((double) n2 * Math.PI / (double) 180) * (radius * (double) -1)), 0, color);
        }
        for (n2 = 0; n2 <= 90; n2 += 3) {
            builder.vertex(mat, (float) (endX - radius + Math.sin((double) n2 * Math.PI / (double) 180) * radius), (float) (endY - radius + Math.cos((double) n2 * Math.PI / (double) 180) * radius), 0, color);
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            builder.vertex(mat, (float) (endX - radius + Math.sin((double) n2 * Math.PI / (double) 180) * radius), (float) (y + radius + Math.cos((double) n2 * Math.PI / (double) 180) * radius), 0, color);
        }
        
        builder.draw();
    }

    public static void renderOutline(MatrixStack i, int x, int y, int endingX, int endingY, int color) {
        IBridge.internal().screen_fill(i, x, y, endingX, y + 1, color);
        IBridge.internal().screen_fill(i, x, endingY, endingX, endingY + 1, color);
        IBridge.internal().screen_fill(i, x, y, x + 1, endingY, color);
        IBridge.internal().screen_fill(i, endingX, y, endingX + 1, endingY, color);
    }

    public static void renderCircle(double x, double y, float halfRadius, int color) {
        drawRoundedRect(x - halfRadius, y - halfRadius, x + halfRadius, y + halfRadius, halfRadius * 2, color);
    }

    public static boolean basicCollisionCheck(double mouseX, double mouseY, double x, double y, double endX, double endY) {
        double val = x;
        if(endX < x) {
            x = endX;
            endX = val;
        }

        val = y;
        if(endY < y) {
            y = endY;
            endY = val;
        }

        return mouseX >= x & mouseX <= endX & mouseY >= y & mouseY <= endY;
    }

    public static float[] getIdealRenderingPosForText(String text, double x, double y, double endX, double endY) {
        return getIdealRenderingPosForText(text, x, y, endX, endY, 1f);
    }

    /**
        found this in arduino source code
        the value x is between in_min and in_max
        and the value gets on out_min and out_max
        idk how to tell you this
     */
    public static double map(double val, double in_min, double in_max, double out_min, double out_max)
    {
        return (val - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static float[] getIdealRenderingPosForText(String text, double x, double y, double endX, double endY, float scale) {
        double val = endX;
        if(x > endX) {
            endX = x;
            x = val;
        }

        val = endY;
        if(y > endY) {
            endY = y;
            y = val;
        }

        float[] renderingPos = new float[2];

        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraft.getTextRenderer();
        int textWidth = textRenderer.getWidth(text);
        int textHeight = textRenderer.fontHeight();

        // Adjust the rendering position based on the specified parameters and scale
        renderingPos[0] = (float) ((x + endX) / 2 - textWidth / 2 * scale);
        renderingPos[1] = (float) ((y + endY) / 2 - textHeight / 2 * scale);

        return renderingPos;
    }

    public static void enableScissor(float x, float y, float endX, float endY) {
        var width = endX - x;
        var height = endY - y;

        IBridge.internal().GL11_enableScissor((int) x, (int) y, (int) width, (int) height);
    }

    public static void disableScissor() {
        IBridge.internal().GL11_disableScissor();
    }

    public static String capitalize(String string) {
        if(string.contains(" ")) {
            String[] strE = string.split(" ");
            List<String> strEL = new ArrayList<>();
            for(String strELR : strE) {
                if(strELR.length() >= 2) {
                    strEL.add(strELR.toUpperCase().charAt(0) + strELR.toLowerCase().substring(1));
                } else {
                    strEL.add(strELR);
                }
            }
            string = String.join(" ", strEL.toArray(CharSequence[]::new));
        } else {
            string = string.toUpperCase().charAt(0) + string.toLowerCase().substring(1);
        }

        return string;
    }
}
