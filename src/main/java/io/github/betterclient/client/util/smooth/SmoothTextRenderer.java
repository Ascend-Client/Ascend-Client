package io.github.betterclient.client.util.smooth;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.impl.other.SmoothFont;
import io.github.betterclient.client.util.FileResource;

import java.awt.*;
import java.io.IOException;

public class SmoothTextRenderer implements IBridge.TextRenderer {
    public static SmoothTextRenderer instance;
    static {
        try {
            instance = new SmoothTextRenderer(new FontGlyphInfo(SmoothFont.instance.font.value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FontGlyphInfo info;
    public IBridge.Identifier texture = new IBridge.Identifier("/ascendfont" + SmoothFont.instance.font.value.toLowerCase() + ".png");

    public SmoothTextRenderer(FontGlyphInfo glyph) {
        this.info = glyph;

        boolean initResource = true;
        for (IBridge.Resource value : Ascend.getInstance().resources.values()) {
            if(value instanceof FileResource fres && fres.s.equals("/ascend/fonts/" + SmoothFont.instance.font.value + ".png")) {
                initResource = false;
            }
        }

        if (initResource) {
            Ascend.getInstance().resources.put(texture, new FileResource("/ascend/fonts/" + SmoothFont.instance.font.value + ".png"));
        }
    }

    @Override
    public void draw(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        internalDraw(matrices, text, x, y, color, false);
    }

    @Override
    public void draw(IBridge.MatrixStack matrices, IBridge.Text text, float x, float y, int color) {
        internalDraw(matrices, text.str, x, y, color, text.underline);
    }

    @Override
    public int bs$getWidth(String text) {
        int cw = 0;
        float scale = 9f / getLineHeight(text);
        scale-=.06f;

        for (int i : text.chars().toArray()) {
            if(this.info.containsChar(i)) {
                cw += (int) (this.info.getChar(i).xad() * scale);
            }
        }
        return cw;
    }

    @Override
    public int fontHeight() {
        return 10;
    }

    @Override
    public void drawWithShadow(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        float red = (float)(color >> 16 & 255) / 255.0F * 0.25f;
        float green = (float)(color >> 8 & 255) / 255.0F * 0.25f;
        float blue = (float)(color & 255) / 255.0F * 0.25f;
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        internalDraw(matrices, text, x + 1, y + 1, new Color(red, green, blue, alpha).getRGB(), false);
        internalDraw(matrices, text, x, y, color, false);
    }

    private void internalDraw(IBridge.MatrixStack matrices, String str, float x, float y, int color, boolean underline) {
        IBridge.MinecraftClient client = IBridge.MinecraftClient.getInstance();

        float oX = x;
        float scale = 9f / getLineHeight(str);
        scale-=.06f;
        client.setShaderTexture(0, this.texture);
        client.setShaderColor(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f, ((color >> 24) & 0xFF) / 255f);
        matrices.bs$push();
        matrices.bs$translate(x,y,1);
        matrices.bs$scale(scale, scale, scale);
        matrices.bs$translate(-x,-y,1);
        for (int i : str.chars().toArray()) {
            if(this.info.containsChar(i)) {
                FontCharacter fontCharacter = this.info.getChar(i);

                int orW = fontCharacter.originalW();
                int orH = fontCharacter.originalH();

                if(orW == 0) orW++;
                if(orH == 0) orH++;

                int offX = fontCharacter.xoff();
                int offY = fontCharacter.yoff();
                if(i == '-') offY = 0;

                float crx = x + offX;
                float cry = y + offY;

                IBridge.internal().drawTexture(
                        matrices,
                        (int) crx,
                        (int) cry,
                        fontCharacter.x(),
                        fontCharacter.y(),
                        orW,
                        orH,
                        256,
                        256
                );

                x+=fontCharacter.xad();
            } else {
                x+=1;
            }
        }

        matrices.bs$pop();

        if(underline) {
            IBridge.internal().screen_fill(matrices, (int) oX, (int) (y + 10), (int) oX + (this.bs$getWidth(str)), (int) (y + 11), -1);
        }
    }

    private float getLineHeight(String str) {
        int highest = 0;
        for (FontCharacter aChar : this.info.chars) {
            if(aChar.height() > highest && str.contains(aChar.id() + ""))
                highest = aChar.height();
        }
        return highest;
    }
}
