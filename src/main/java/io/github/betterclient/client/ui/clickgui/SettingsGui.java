package io.github.betterclient.client.ui.clickgui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.*;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;
import java.util.Objects;

public class SettingsGui extends Screen {
    private int w2, h2, c1, c2, c3, numHold;

    public final Module module;
    public boolean isModeExtended = false, isNumberHolding = false, holdColor = false, isKeyListening = false, isColorExtended = false, goBackToOtherMods = false;
    public ModeSetting extendedModeSetting = null;
    public NumberSetting extendedNumberSetting = null;
    public ColorSetting extendedColorSetting = null;
    public KeyBindSetting listeningKeyBind = null;
    private int colX, colY;
    public int scY, max;
    private int alpha;
    private int alphaminY;
    private float hue;
    private boolean isColorHolding, isHueHolding, isAlphaHolding;
    private int hueminY;

    public SettingsGui(Module module) {
        super();
        this.module = module;

        StackTraceElement[] elements = new Throwable().getStackTrace();
        if(elements[1].getClassName().contains("ClickGui")) {
            goBackToOtherMods = true;
        }
    }

    @Override
    protected void init() {
        this.w2 = width / 2;
        this.h2 = height / 2;
        this.c1 = new Color(0, 0, 0, 84).getRGB();
        this.c2 = new Color(255, 255, 255, 180).getRGB();
        this.c3 = new Color(255, 0, 0).getRGB();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(matrices);
        TextRenderer renderer = MinecraftClient.getInstance().getTextRenderer();
        UIUtil.drawRoundedRect(w2 - 200, h2 - 190, w2 + 200, h2 + 190, 10f, c1);

        int w3 = renderer.bs$getWidth("<- Back");
        fill(matrices, w2 - 200, h2 - 168, w2 - 188 + w3, h2 - 180, c2);
        fill(matrices, w2 - 190, h2 - 180, w2 - 188 + w3, h2 - 190, c2);

        UIUtil.enableScissor(w2 - 200, h2 - 190, w2 - 190, h2 - 180);
        UIUtil.drawRoundedRect(w2 - 200, h2 - 190, w2 - 180, h2 - 170, 10f, c2);
        UIUtil.disableScissor();

        float[] pos = UIUtil.getIdealRenderingPosForText("<- Back", w2 - 188 + w3, h2 - 190, w2 - 200, h2 - 170);
        Text text = Text.literal("<- Back").withStyle(
                Style.withUnderline(
                        UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 188 + w3, h2 - 190, w2 - 200, h2 - 170)
                )
        );
        renderer.draw(matrices, text, pos[0], pos[1], -1);
        fill(matrices, w2 - 200, h2 - 170, w2 + 200, h2 - 168, -1);
        fill(matrices, w2 - 188 + w3, h2 - 190, w2 - 186 + w3, h2 - 170, -1);

        pos = UIUtil.getIdealRenderingPosForText(this.module.name, w2 - 200, h2 - 190, w2 + 200, h2 - 170);
        renderer.draw(matrices, this.module.name, pos[0], pos[1], -1);

        if(this.module instanceof HUDModule hudmod) {
            renderer.draw(matrices, "Preview", w2 + 187 - hudmod.renderable.width, h2 + 175 - hudmod.renderable.height - 10, -1);
            UIUtil.drawRoundedRect(w2 + 185 - hudmod.renderable.width, h2 + 175 - hudmod.renderable.height, w2 + 195, h2 + 185, 2f, c1);

            if(hudmod.forceVanillaFont.value) {
                hudmod.renderable.textRenderer = IBridge.MinecraftClient.getInstance().getMCRenderer();
            } else {
                hudmod.renderable.textRenderer = IBridge.MinecraftClient.getInstance().getTextRenderer();
            }
            hudmod.renderable.renderWithXY(w2 + 190 - hudmod.renderable.width, h2 + 180 - hudmod.renderable.height);
        }

        if(this.isNumberHolding && this.extendedNumberSetting != null) {
            int mouseHold = mouseX + numHold;
            if(mouseHold < (w2 - 188) + renderer.bs$getWidth(this.extendedNumberSetting.name) + 30)
                mouseHold = (w2 - 188) + renderer.bs$getWidth(this.extendedNumberSetting.name) + 30;

            if(mouseHold > w2 + 165)
                mouseHold = w2 + 165;

            this.extendedNumberSetting.value = (int) UIUtil.map(mouseHold, (w2 - 188) + renderer.bs$getWidth(this.extendedNumberSetting.name) + 30, w2 + 165, this.extendedNumberSetting.min, this.extendedNumberSetting.max);
        }

        if(this.isAlphaHolding && this.extendedColorSetting != null) {
            int yY = mouseY - alphaminY;
            if(yY < 0) yY = 0;
            if(yY > 120) yY = 119;

            this.alpha = (int) (255 * (1 - (float) (yY) / 120));
            this.extendedColorSetting.setColor(new Color(
                    extendedColorSetting.getColor().getRed(),
                    extendedColorSetting.getColor().getGreen(),
                    extendedColorSetting.getColor().getBlue(),
                    alpha
            ));
        }

        if(this.isHueHolding && this.extendedColorSetting != null) {
            int yY = mouseY - hueminY;
            if(yY <= 0) yY = 0;
            if(yY >= 120) yY = 119;

            this.hue = (float) (yY) / 120;
            float[] oldValues = Color.RGBtoHSB(extendedColorSetting.getColor().getRed(), extendedColorSetting.getColor().getGreen(), extendedColorSetting.getColor().getBlue(), null);

            Color newColor = Color.getHSBColor(this.hue, oldValues[1], oldValues[2]);
            this.extendedColorSetting.setColor(new Color(
                    newColor.getRed(),
                    newColor.getGreen(),
                    newColor.getBlue(),
                    alpha
            ));
        }

        int curY = h2 - 160 - scY;
        UIUtil.enableScissor(w2 - 200, h2 - 168, w2 + 200, h2 + 190);
        for(Setting set : this.module.getSettings()) {
            if(set instanceof NoneSetting) {
                renderer.draw(matrices, set.name, w2 - (renderer.bs$getWidth(set.name) / 2f), curY, c3);
                curY += 30;
                continue;
            }

            int drawX = w2 - 188;
            int endDrawX = renderer.bs$getWidth(set.name);

            if(set instanceof BooleanSetting bool) {
                UIUtil.drawRoundedRect(drawX, curY - 2.5, drawX + 15, curY + 12.5, 2f, c1);
                if(bool.isValue()) {
                    pos = UIUtil.getIdealRenderingPosForText("+", drawX, curY - 2.5, drawX + 15, curY + 12.5);
                    renderer.draw(matrices, "+", pos[0], pos[1], -1);
                }
                drawX += 20;
            } else if(set instanceof ModeSetting mode) {
                int width = 75;
                for (String value : mode.values) {
                    int gg = renderer.bs$getWidth(value) + 15;
                    if(gg > width) width = gg;
                }

                UIUtil.drawRoundedRect(drawX + endDrawX + 5, curY - 2.5, drawX + endDrawX + width + 5, curY + 12.5, 2f, c1);
                renderer.draw(matrices, mode.value, drawX + endDrawX + 7, curY + 1, -1);
                renderer.draw(matrices, "v", drawX + endDrawX + width - 3, curY + 1, -1);

                if(mode.equals(this.extendedModeSetting) && this.isModeExtended) {
                    int height = mode.values.size() * 12;
                    int currY = curY + 13;
                    UIUtil.drawRoundedRect(drawX + endDrawX + 5, curY + 13, drawX + endDrawX + width + 5, curY + 13 + height, 2f, c1);
                    for (String value : mode.values) {
                        fill(matrices, drawX + endDrawX + 5, currY, drawX + endDrawX + width + 5, currY + 10, Objects.equals(value, mode.value) ? c3 : (UIUtil.basicCollisionCheck(mouseX, mouseY, drawX + endDrawX + 5, currY, drawX + endDrawX + width + 5, currY + 10) ? c1 : c2));
                        renderer.draw(matrices, value, drawX + endDrawX + 5, currY + 1.5f, -1);
                        currY += 12;
                    }
                }
            } else if(set instanceof NumberSetting number) {
                renderer.draw(matrices, number.min + "", drawX + endDrawX + 30 - (renderer.bs$getWidth(number.min + "")), curY, c3);
                renderer.draw(matrices, number.max + "", w2 + 167, curY, c3);

                UIUtil.drawRoundedRect(drawX + endDrawX + 30, curY + 3, w2 + 165, curY + 5, 2f, -1);
                float area = (float) UIUtil.map(number.value, number.min, number.max, drawX + endDrawX + 30, w2 + 165);
                renderer.draw(matrices, number.value + "", area - (renderer.bs$getWidth(number.value + "") / 2f), curY + 10, -1);

                UIUtil.drawRoundedRect(area - 5, curY - 1, area + 5, curY + 9, 10f, c3);
            } else if(set instanceof KeyBindSetting key) {
                UIUtil.drawRoundedRect(w2 + 135, curY, w2 + 195, curY + 15, 5f, c1);
                String text1 = MinecraftClient.getInstance().getKeyName(key.key, 0);
                if(isKeyListening) text1 = "Listening";

                pos = UIUtil.getIdealRenderingPosForText(text1, w2 + 135, curY, w2 + 195, curY + 15);
                renderer.draw(matrices, text1, pos[0], pos[1], -1);
            } else if(set instanceof ColorSetting color) {
                UIUtil.drawRoundedRect(w2 + 175, curY, w2 + 195, curY + 15, 5f, color.getColor().getRGB());

                if(color.equals(this.extendedColorSetting) && this.isColorExtended) {
                    int widthh = 100;
                    int heightt = 100;

                    int radius = widthh / 2 - 40;
                    Point center = new Point(widthh / 2, heightt / 2);

                    int dx = -4214, dy = -4214;
                    for (int y = 0; y < 120; y++) {
                        float h = (float) y / 120;
                        Color clr = Color.getHSBColor(h, 1, 1);
                        if(h == hue) {
                            dx = w2 - 5;
                            dy = y + curY + 15;
                        }

                        fill(matrices, w2 - 10, y + curY + 15, w2, y + curY + 16, clr.getRGB());
                    }

                    if(dx != -4214)
                        UIUtil.renderCircle(dx, dy, 4f, -1);

                    dx = -4214;
                    for (int y = 0; y < 120; y++) {
                        int a = (int) (255 * (1 - (float) y / 120));
                        Color clr = new Color(120, 120, 120, a);
                        if(a == alpha) {
                            dx = w2 - 35;
                            dy = y + curY + 15;
                        }

                        fill(matrices, w2 - 30, y + curY + 15, w2 - 40, y + curY + 16, clr.getRGB());
                    }

                    if(dx != -4214)
                        UIUtil.renderCircle(dx, dy, 4f, -1);

                    int pickX = 0, pickY = 0;
                    int pickDiff = 256*256*256;
                    int squareSize = radius + 20;
                    int startX = center.x - squareSize / 2;
                    int startY = center.y - squareSize / 2;
                    for (int y = 0; y < squareSize; y++) {
                        for (int x = 0; x < squareSize; x++) {
                            float saturation = (float) x / (squareSize - 1);
                            float brightness = 1 - (float) y / (squareSize - 1);

                            Color clr = Color.getHSBColor(hue, saturation, brightness);

                            int xx = w2 - 5 + startX + x*4;
                            int yy = startY + y*4 + curY - 20;

                            fill(matrices, xx, yy, xx+4, yy+4, clr.getRGB());

                            if(colX != 69 && !isColorHolding) {
                                if(UIUtil.basicCollisionCheck(colX, colY, xx, yy, xx + 4, yy + 4)) {
                                    color.setColor(new Color(
                                            clr.getRed(),
                                            clr.getGreen(),
                                            clr.getBlue(),
                                            alpha
                                    ));
                                }
                            }

                            if(isColorHolding && UIUtil.basicCollisionCheck(mouseX, mouseY, xx, yy, xx + 4, yy + 4)) {
                                this.colX = mouseX;
                                this.colY = mouseY;

                                Color newColor = Color.getHSBColor(this.hue, saturation, brightness);
                                this.extendedColorSetting.setColor(new Color(
                                        newColor.getRed(),
                                        newColor.getGreen(),
                                        newColor.getBlue(),
                                        alpha
                                ));
                            }

                            if(pickDiff > calcDiff(clr, color.getColor())) {
                                pickX = startX + x*4;
                                pickY = startY + y*4;
                                pickDiff = calcDiff(clr, color.getColor());
                            }
                        }
                    }

                    pickX+=w2 - 5;
                    pickY+=curY - 20;

                    if(colX == 69) {
                        colX = pickX;
                        colY = pickY;
                    } else {
                        pickX = colX;
                        pickY = colY;
                    }

                    UIUtil.renderCircle(pickX, pickY, 4f, invertColor(color.getColor()).getRGB());
                    renderer.draw(matrices, set.name, drawX, curY, -1);
                }
            }

            renderer.draw(matrices, set.name, drawX, curY, -1);
            curY += 20;

            if(this.isColorExtended && this.extendedColorSetting == set)
                curY += 125;
        }

        max = curY + scY - (h2 + 190);
        if((curY + scY) <= (h2 + 190)) {
            scY = 0;
            max = 0;
        }
        UIUtil.disableScissor();

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextRenderer renderer = MinecraftClient.getInstance().getTextRenderer();
        int w3 = renderer.bs$getWidth("<- Back");
        if(button == 0 && UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 188 + w3, h2 - 190, w2 - 200, h2 - 170)) {
            if(goBackToOtherMods)
                MinecraftClient.getInstance().setGuiScreen(new ClickGui());
            else
                MinecraftClient.getInstance().setGuiScreen(new HUDMoveUI());
        }

        if(button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int curY = h2 - 160 - scY;
        for(Setting set : this.module.getSettings()) {
            if(set instanceof NoneSetting) {
                curY += 30;
                continue;
            }

            int drawX = w2 - 188;
            int endDrawX = renderer.bs$getWidth(set.name);

            if(set instanceof BooleanSetting bool) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, drawX, curY - 2.5, drawX + 15, curY + 12.5)) {
                    bool.toggle();
                }
            } else if(set instanceof ModeSetting mode) {
                int width = 75;
                for (String value : mode.values) {
                    int gg = renderer.bs$getWidth(value) + 15;
                    if(gg > width) width = gg;
                }

                if(UIUtil.basicCollisionCheck(mouseX, mouseY, drawX + endDrawX + 5, curY - 2.5, drawX + endDrawX + width + 5, curY + 12.5) && !this.isColorExtended && !this.isKeyListening) {
                    this.extendedModeSetting = mode;
                    this.isModeExtended = !this.isModeExtended;
                }

                if(mode.equals(this.extendedModeSetting) && this.isModeExtended) {
                    int currY = curY + 13;
                    for (String value : mode.values) {
                        if(UIUtil.basicCollisionCheck(mouseX, mouseY, drawX + endDrawX + 5, currY, drawX + endDrawX + width + 5, currY + 10)) {
                            while (!mode.value.equals(value)) mode.toggle();
                            this.isModeExtended = false;
                            this.extendedColorSetting = null;
                        }
                        currY += 12;
                    }
                }
            } else if(set instanceof NumberSetting number) {
                float area = (float) UIUtil.map(number.value, number.min, number.max, drawX + endDrawX + 30, w2 + 165);

                if(UIUtil.basicCollisionCheck(mouseX, mouseY, area - 5, curY - 1, area + 5, curY + 9)) {
                    this.isNumberHolding = true;
                    this.extendedNumberSetting = number;
                    this.numHold = (int) (mouseX - area);
                } else if(UIUtil.basicCollisionCheck(mouseX, mouseY, drawX + endDrawX + 30, curY + 3, w2 + 165, curY + 5)) {
                    this.isNumberHolding = true;
                    this.extendedNumberSetting = number;
                    this.numHold = 5;
                }
            } else if(set instanceof KeyBindSetting key) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 + 135, curY, w2 + 195, curY + 15) && !this.isColorExtended && !this.isModeExtended) {
                    if(this.isKeyListening) this.isKeyListening = false;
                    else {
                        this.isKeyListening = true;
                        this.listeningKeyBind = key;
                    }
                }
            } else if(set instanceof ColorSetting color) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 + 175, curY, w2 + 195, curY + 15) && !this.isModeExtended && !this.isKeyListening) {
                    this.isColorExtended = !this.isColorExtended;
                    this.extendedColorSetting = color;

                    if(this.isColorExtended) {
                        colX = 69;
                        this.alpha = color.getColor().getAlpha();
                        this.hue = rgbToHue(color) / 360f;
                    }

                    curY+=15;
                    continue;
                }

                if(this.isColorExtended && this.extendedColorSetting == color) {
                    int widthh = 100;
                    int heightt = 100;

                    int radius = widthh / 2 - 40;
                    Point center = new Point(widthh / 2, heightt / 2);

                    for (int y = 0; y < 120; y++) {
                        float h = (float) y / 120;
                        if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 10, y + curY + 15, w2, y + curY + 16)) {
                            this.hueminY = curY + 15;
                            this.hue = h;
                            this.isHueHolding = true;
                        }
                    }

                    for (int y = 0; y < 120; y++) {
                        int a = (int) (255 * (1 - (float) y / 120));
                        if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 30, y + curY + 15, w2 - 40, y + curY + 16)) {
                            this.alpha = a;
                            this.alphaminY = curY + 15;
                            this.isAlphaHolding = true;
                        }
                    }

                    int squareSize = radius + 20;
                    int startX = center.x - squareSize / 2;
                    int startY = center.y - squareSize / 2;
                    for (int y = 0; y < squareSize; y++) {
                        for (int x = 0; x < squareSize; x++) {
                            int xx = w2 - 5 + startX + x*4;
                            int yy = startY + y*4 + curY - 20;

                            if (UIUtil.basicCollisionCheck(mouseX, mouseY, xx, yy, xx + 4, yy + 4)) {
                                this.isColorHolding = true;
                                break;
                            }
                        }
                    }

                    curY += 125;
                }
            }

            curY += 20;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private float rgbToHue(ColorSetting color) {
        float rf = color.getColor().getRed() / 255.0f;
        float gf = color.getColor().getGreen() / 255.0f;
        float bf = color.getColor().getBlue() / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float data;
        if (delta == 0) {
            data = 0;
        } else if (max == rf) {
            data = 60 * (((gf - bf) / delta) % 6);
        } else if (max == gf) {
            data = 60 * (((bf - rf) / delta) + 2);
        } else {
            data = 60 * (((rf - gf) / delta) + 4);
        }

        if (data < 0) {
            data += 360;
        }
        return data;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(holdColor || isNumberHolding || isAlphaHolding || isColorHolding || isHueHolding)
            BallSack.getInstance().config.save();

        this.holdColor = false;
        this.isNumberHolding = false;
        this.isAlphaHolding = false;
        this.isColorHolding = false;
        this.isHueHolding = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 200, h2 - 190, w2 + 200, h2 + 190)) {
            int newValue = scY;
            if(amount == -1) {
                newValue = scY + 5;
                if (newValue > max) {
                    newValue = max;
                }
            } else if(amount == 1){
                newValue = scY - 5;
                if (newValue < 0) {
                    newValue = 0;
                }
            }
            scY = newValue;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public Color invertColor(Color color) {
        int red = 255 - color.getRed();
        int green = 255 - color.getGreen();
        int blue = 255 - color.getBlue();
        return new Color(red, green, blue);
    }

    public int calcDiff(Color color1, Color color) {
        int currDiff;
        int r = Math.max(color1.getRed(), color.getRed()) - Math.min(color1.getRed(), color.getRed()) + 1;
        int g = Math.max(color1.getGreen(), color.getGreen()) - Math.min(color1.getGreen(), color.getGreen()) + 1;
        int b = Math.max(color1.getBlue(), color.getBlue()) - Math.min(color1.getBlue(), color.getBlue()) + 1;

        currDiff = r * g * b;
        return currDiff;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(isKeyListening && keyCode != IBridge.getKeys().KEY_ESCAPE) {
            listeningKeyBind.key = keyCode;
            try {
                listeningKeyBind.bind.setKey(keyCode);
                BallSack.getInstance().config.save();
            } catch (Exception e) {
                IBridge.getPreLaunch().error(e.toString());
            }

            isKeyListening = false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
