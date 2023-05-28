package io.github.betterclient.client.ui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.*;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class SettingsUI extends Screen {
    public double x, y;
    public Module mod;
    public TextRenderer tr = MinecraftClient.getInstance().textRenderer;

    public boolean settingColor = false;
    public ColorSetting setting = null;

    public NumberSetting redColor = new NumberSetting("Red", 0, 0, 255);
    public NumberSetting greenColor = new NumberSetting("Green", 0, 0, 255);
    public NumberSetting blueColor = new NumberSetting("Blue", 0, 0, 255);
    public NumberSetting alphaColor = new NumberSetting("Alpha", 0, 0, 255);

    public boolean settingNumber = false;
    public NumberSetting number = null;
    public int holdX = 0;

    public boolean settingBind = false;
    public KeyBindSetting bindSetting = null;

    public int uiwidth = 400;
    public int uiheight = 300;

    boolean goBackToOtherMods = false;

    public SettingsUI(Module mod) {
        super(Text.of(""));
        this.mod = mod;

        StackTraceElement[] elements = new Throwable().getStackTrace();
        if(elements[1].getClassName().contains("OtherModsUI")) {
            goBackToOtherMods = true;
        }
    }

    @Override
    protected void init() {
        x = (width / 2) - (uiwidth / 2);
        y = (height / 2) - (uiheight / 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        render(mouseX, mouseY);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseClick(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseRelease(mouseX, mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode != GLFW.GLFW_KEY_ESCAPE)
            onKeyboard(keyCode);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void render(double mouseX, double mouseY) {
        UIUtil.drawRoundedRect(x, y, x + uiwidth, y + uiheight, 20f, new Color(0, 0, 0, 84).getRGB());

        UIUtil.drawRoundedRect(x + uiwidth - 50, y + uiheight - 25, x + uiwidth - 5, y + uiheight - 5, 10f, new Color(255, 255, 255, 120).getRGB());
        int[] pos = UIUtil.getIdealRenderingPosForText("Close", x + uiwidth - 50, y + uiheight - 25, x + uiwidth - 5, y + uiheight - 5);

        tr.draw(
                new MatrixStack(),
                new LiteralText("Close")
                        .setStyle(
                                Style
                                        .EMPTY
                                        .withUnderline(
                                                UIUtil.basicCollisionCheck(
                                                        mouseX, mouseY,
                                                        x + uiwidth - 50, y + uiheight - 25,
                                                        x + uiwidth - 5, y + uiheight - 5
                                                )
                                        )
                        ),
                pos[0],
                pos[1],
                -1
        );

        float y = (float) (this.y + 10F);
        tr.draw(new MatrixStack(), mod.getName() + " - Settings", (float) (x + 10), y, new Color(255, 0, 0).getRGB());
        y+=15;

        for (Setting set : this.mod.getSettings()) {
            tr.draw(new MatrixStack(), set.name, (float) (x + 10F), y, -1);

            if(set instanceof BooleanSetting bool) {
                UIUtil.drawRoundedRect(x + uiwidth - 50, y, x + uiwidth - 2, y + 15, 5F, (bool.value ? new Color(255, 255, 255, 84) : new Color(0, 0, 0, 84)).getRGB());
                tr.draw(new MatrixStack(),  bool.value ? "Enabled" : "Disabled", (float) (x + uiwidth - 48), y + 1, -1);
            }

            if(set instanceof ModeSetting mode) {
                UIUtil.drawRoundedRect(x + uiwidth - 50, y, x + uiwidth - 2, y + 15, 5F, new Color(0, 0, 0, 84).getRGB());
                tr.draw(new MatrixStack(), mode.value, (float) (x + uiwidth - 48), y + 1, -1);
            }

            if(set instanceof ColorSetting color) {
                UIUtil.drawRoundedRect(x + uiwidth - 50, y, x + uiwidth - 2, y + 15, 5F, (setting == color ? new Color(255, 255, 255, 84) : new Color(0, 0, 0, 84)).getRGB());

                if(this.setting == color && settingColor) {
                    y+=10;
                    for (int i = 0; i < 4; i++) {
                        y+=20;

                        NumberSetting num;
                        int red, green, blue, alpha;
                        Color c = color.getColor();

                        red = c.getRed();
                        green = c.getGreen();
                        blue = c.getBlue();
                        alpha = c.getAlpha();

                        if(i == 0) {
                            num = redColor;
                            red = num.value;
                        } else if(i == 1) {
                            num = greenColor;
                            green = num.value;
                        } else if(i == 2) {
                            num = blueColor;
                            blue = num.value;
                        } else {
                            num = alphaColor;
                            alpha = num.value;
                        }

                        tr.draw(new MatrixStack(), num.name, (float) (x + 10F), y, -1);

                        UIUtil.drawRoundedRect(x + uiwidth - 232, y + 6, x + uiwidth - 32, y + 9, 1F, new Color(0, 0, 0, 84).getRGB());
                        tr.draw(new MatrixStack(), num.value + "", (float) (x + uiwidth - 28), y + 2, -1);

                        int renderX = (int) (x + uiwidth - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                        UIUtil.drawRoundedRect(renderX - 7, y, renderX + 7, y + 15, 15F, Color.BLUE.getRGB());

                        color.setColor(new Color(red, green, blue, alpha));
                    }
                    y+=10;
                }
            }

            if(set instanceof NumberSetting num) {
                UIUtil.drawRoundedRect(x + uiwidth - 232, y + 6, x + uiwidth - 32, y + 9, 1F, new Color(0, 0, 0, 84).getRGB());
                tr.draw(new MatrixStack(), num.value + "", (float) (x + uiwidth - 28), y + 2, -1);

                int renderX = (int) (x + uiwidth - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                UIUtil.drawRoundedRect(renderX - 7, y, renderX + 7, y + 15, 15F, Color.BLUE.getRGB());
            }

            if(set instanceof KeyBindSetting key) {
                UIUtil.drawRoundedRect(x + uiwidth - 60, y, x + uiwidth - 12, y + 15, 5F, new Color(0, 0, 0, 84).getRGB());
                String kys = GLFW.glfwGetKeyName(key.key, 0);
                if(settingBind)
                    kys = "Listening";

                int[] poss = UIUtil.getIdealRenderingPosForText(kys, x + uiwidth - 60, y, x + uiwidth - 12, y + 15);
                tr.draw(new MatrixStack(), kys, poss[0], poss[1], -1);
            }

            y+=20;
        }

        if(this.settingNumber) {
            double val = ((mouseX - holdX) - (x + uiwidth - 232));

            if(val > 200)
                val = 200;

            if(val < 0)
                val = 0;

            this.number.value = (int) UIUtil.map(val, 0, 200, this.number.min, this.number.max);
        }
    }

    public void mouseClick(double mouseX, double mouseY, int button) {
        float currentY = (float) (this.y + 25F);

        for (Setting set : this.mod.getSettings()) {
            if(set instanceof BooleanSetting bool) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + uiwidth - 50, currentY, x + uiwidth - 2, currentY + 15) && button == 0) {
                    bool.toggle();
                    BallSack.getInstance().config.save();
                }
            }

            if(set instanceof ModeSetting mode) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + uiwidth - 50, currentY, x + uiwidth - 2, currentY + 15) && button == 0) {
                    mode.toggle();
                    BallSack.getInstance().config.save();
                }
            }

            if(set instanceof ColorSetting color) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + uiwidth - 50, currentY, x + uiwidth - 2, currentY + 15) && button == 0) {
                    if(this.settingColor) {
                        if(this.setting == color) {
                            this.settingColor = false;
                            this.setting = null;
                        }
                        else {
                            this.setting = color;

                            Color c = color.getColor();
                            this.redColor.value = c.getRed();
                            this.greenColor.value = c.getGreen();
                            this.blueColor.value = c.getBlue();
                            this.alphaColor.value = c.getAlpha();
                        }
                    } else {
                        this.settingColor = true;
                        this.setting = color;

                        Color c = color.getColor();
                        this.redColor.value = c.getRed();
                        this.greenColor.value = c.getGreen();
                        this.blueColor.value = c.getBlue();
                        this.alphaColor.value = c.getAlpha();
                    }
                }

                if(this.settingColor && this.setting == color) {
                    currentY+=10;
                    for (int i = 0; i < 4; i++) {
                        currentY+=20;

                        NumberSetting num;

                        if(i == 0) {
                            num = redColor;
                        } else if(i == 1) {
                            num = greenColor;
                        } else if(i == 2) {
                            num = blueColor;
                        } else {
                            num = alphaColor;
                        }

                        int renderX = (int) (x + uiwidth - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                        if(UIUtil.basicCollisionCheck(mouseX, mouseY, renderX - 7, currentY, renderX + 7, currentY + 15) && button == 0) {
                            this.settingNumber = true;
                            this.number = num;
                            this.holdX = (int) (mouseX - renderX);
                        }

                    }
                    currentY+=10;
                }
            }

            if(set instanceof NumberSetting num) {
                int renderX = (int) (x + uiwidth - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, renderX - 7, currentY, renderX + 7, currentY + 15) && button == 0) {
                    this.settingNumber = true;
                    this.number = num;
                    this.holdX = (int) (mouseX - renderX);
                }
            }

            if(set instanceof KeyBindSetting key) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + uiwidth - 60, currentY, x + uiwidth - 12, currentY + 15) && button == 0) {
                    settingBind = true;
                    bindSetting = key;
                    BallSack.getInstance().config.save();
                }
            }

            currentY+=20;
        }
    }

    public void mouseRelease(double mouseX, double mouseY, int button) {
        if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + uiwidth - 50, y + uiheight - 25, x + uiwidth - 5, y + uiheight - 5) && button == 0) {
            if(goBackToOtherMods)
                MinecraftClient.getInstance().openScreen(new OtherModsUI());
            else
                MinecraftClient.getInstance().openScreen(new HUDMoveUI());
        }

        if(button == 0) {
            number = null;
            settingNumber = false;
            holdX = 0;
            BallSack.getInstance().config.save();
        }
    }

    public void onKeyboard(int key) {
        if(settingBind) {
            bindSetting.key = key;
            try {
                bindSetting.bind.setBoundKey(InputUtil.fromKeyCode(key, 0));
                BallSack.getInstance().config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        settingBind = false;
    }
}
