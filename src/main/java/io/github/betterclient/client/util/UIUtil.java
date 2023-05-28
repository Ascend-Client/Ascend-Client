package io.github.betterclient.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

import static org.lwjgl.opengl.GL11.*;

public class UIUtil {
    /**
     * Totally not skidded from:
     * https://github.com/TitanicClient/Client/blob/bac90137dc4d7724a88eff07c1edde6b74215185/Shared/src/main/java/cc/noxiuam/titanic/client/ui/util/RenderUtil.java#L129
     * @param x x pos
     * @param y y pos
     * @param width this is actually the ending x (x + uiwidth)
     * @param height this is actually the ending y (y + uiheight)
     * @param radius radius of round
     * @param color rect color
     */
    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        int n2;
        float f = (float) (color >> 24 & 0xFF) / (float) 255;
        float f2 = (float) (color >> 16 & 0xFF) / (float) 255;
        float f3 = (float) (color >> 8 & 0xFF) / (float) 255;
        float f4 = (float) (color & 0xFF) / (float) 255;
        glPushAttrib(0);
        glScaled(0.5, 0.5, 0.5);
        x *= 2;
        y *= 2;
        width *= 2;
        height *= 2;
        glEnable(3042);
        glDisable(3553);
        glColor4f(f2, f3, f4, f);
        glEnable(2848);
        glBegin(9);
        for (n2 = 0; n2 <= 90; n2 += 3) {
            glVertex2d(x + radius + Math.sin((double) n2 * (6.5973445528769465 * 0.4761904776096344) / (double) 180) * (radius * (double) -1), y + radius + Math.cos((double) n2 * (42.5 * 0.07391982714328925) / (double) 180) * (radius * (double) -1));
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            glVertex2d(x + radius + Math.sin((double) n2 * (0.5711986642890533 * 5.5) / (double) 180) * (radius * (double) -1), height - radius + Math.cos((double) n2 * (0.21052631735801697 * 14.922564993369743) / (double) 180) * (radius * (double) -1));
        }
        for (n2 = 0; n2 <= 90; n2 += 3) {
            glVertex2d(width - radius + Math.sin((double) n2 * (4.466951941998311 * 0.7032967209815979) / (double) 180) * radius, height - radius + Math.cos((double) n2 * (28.33333396911621 * 0.11087973822685955) / (double) 180) * radius);
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            glVertex2d(width - radius + Math.sin((double) n2 * ((double) 0.6f * 5.2359875479235365) / (double) 180) * radius, y + radius + Math.cos((double) n2 * (2.8529412746429443 * 1.1011767685204017) / (double) 180) * radius);
        }
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2, 2, 2);
        glPopAttrib();
    }

    /**
     * Totally not skidded from:
     * https://github.com/TitanicClient/Client/blob/bac90137dc4d7724a88eff07c1edde6b74215185/Shared/src/main/java/cc/noxiuam/titanic/client/ui/util/RenderUtil.java#LL99C5-L119C6
     * @param resourceLocation
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void drawImage(String resourceLocation, float x, float y, float width, float height) {
        float f5 = width / 2.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        glEnable(3042);

        glBindTexture(GL_TEXTURE_2D, MinecraftClient.getInstance()
                        .getTextureManager()
                        .getTexture(new Identifier(resourceLocation))
                        .getGlId()
        );

        glBegin(7);
        glTexCoord2d(f6 / f5, f7 / f5);
        glVertex2d(x, y);
        glTexCoord2d(f6 / f5, (f7 + f5) / f5);
        glVertex2d(x, y + height);
        glTexCoord2d((f6 + f5) / f5, (f7 + f5) / f5);
        glVertex2d(x + width, y + height);
        glTexCoord2d((f6 + f5) / f5, f7 / f5);
        glVertex2d(x + width, y);
        glEnd();
        glDisable(3042);
    }

    public static boolean basicCollisionCheck(double mouseX, double mouseY, double x, double y, double endX, double endY) {
        return mouseX >= x & mouseX <= endX & mouseY >= y & mouseY <= endY;
    }

    public static int[] getIdealRenderingPosForText(String text, double x, double y, double endX, double endY) {
        return getIdealRenderingPosForText(text, x, y, endX, endY, 1f);
    }

    /*
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

}
