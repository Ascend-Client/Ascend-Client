package io.github.betterclient.client.util;

import static org.lwjgl.opengl.GL11.*;

public class UIUtil {
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
}
