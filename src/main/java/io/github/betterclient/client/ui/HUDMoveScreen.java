package io.github.betterclient.client.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.*;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.*;
import io.github.betterclient.client.util.ClickableBind;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

public class HUDMoveScreen extends Screen {
    public ModuleManager modMan = BallSack.getInstance().moduleManager;
    private static HUDMoveScreen instance;

    public Renderable moving = null;
    public int moveX = 0, moveY = 0;
    public List<HUDModule> hudMods = new Vector<>();

    public boolean isEnabling = false;
    public int enableX = 0, enableY = 0;

    public boolean isEnableOther = false;
    public int otherArea = 0;
    public SettingsRenderer renderer;

    public HUDMoveScreen() {
        super(Text.of(""));
        instance = this;
        hudMods.addAll(modMan.getByCategory(Category.HUD).stream().map(HUDModule::cast).toList());
    }

    @Override
    protected void init() {
        moving = null;

        int x = width - 125;
        int y = 30;
        for (Module mod : modMan.getByCategory(Category.OTHER)) {
            this.addButton(new ModuleEnabler(x, y, 150, 20, mod, this));
            y+=25;
        }
        otherArea = y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        if(moving != null) {
            moving.x = mouseX - moveX;
            moving.y = mouseY - moveY;
        }

        for(HUDModule mod : hudMods) {
            if(!mod.toggled) continue;

            Renderable render = mod.renderable;

            UIUtil.drawRoundedRect(
                    render.x - 4, render.y - 4,
                    render.x + render.width + 4,
                    render.y + render.height + 4,
                    10f, new Color(0, 0, 0, 84).getRGB()
            );

            /*
            Renderable Bug fixing:

            UIUtil.drawRoundedRect(
                    render.x, render.y,
                    render.x + render.width,
                    render.y + render.height,
                    0f, Color.WHITE.getRGB()
            );*/

            matrices = new MatrixStack();

            matrices.push();
            int x = render.x;
            int y = render.y + render.height + 6;

            matrices.translate(x,y,1);
            matrices.scale(0.85f,0.85f,1);
            matrices.translate(-x,-y,1);
            textRenderer.draw(matrices, mod.name, x, y, -1);
            matrices.pop();

            float scale = 0.85f;
            y+= (9 * 0.85) + 1;

            boolean underLine = UIUtil.basicCollisionCheck(
                    mouseX, mouseY,
                    x, y,
                    render.x + render.width, (int) (y + (textRenderer.fontHeight * scale))
            );

            Style style = Style.EMPTY.withUnderline(underLine);

            matrices.translate(x,y,1);
            matrices.scale(scale,scale,1);
            matrices.translate(-x,-y,1);
            textRenderer.draw(matrices, new LiteralText("Disable").setStyle(style), x, y, Color.RED.getRGB());
            matrices.pop();

            mod.render(new RenderEvent());
        }

        if(isEnabling) {
            UIUtil.drawRoundedRect(
                    enableX, enableY,
                    enableX + 100, enableY + 200,
                    20f, new Color(0, 0, 0, 150).getRGB()
            );

            int y = enableY + 10;
            int x = enableX + 5;

            for(HUDModule m : hudMods) {
                if(m.toggled) continue;

                boolean withStyle = false;
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x - 5, y - 10, x + 95, y + 10)) {
                    UIUtil.drawRoundedRect(x - 5, y - 6, x + 95, y + 14, 20f,
                            new Color(0, 0, 0, 150).getRGB());
                    withStyle = true;
                }

                textRenderer.draw(new MatrixStack(),
                        new LiteralText(m.name).setStyle(Style.EMPTY.withUnderline(withStyle)),
                         x, y, -1
                );

                y+=20;
            }
        }

        UIUtil.drawRoundedRect(width - 120, 5, width - 5, 25,
                5F, new Color(0, 0, 0, 120).getRGB());

        String text = (isEnableOther ? "˅" : ">") + " Enable Mods";

        int[] renderPos = UIUtil.getIdealRenderingPosForText(text, width - 100, 5, width - 5, 25);

        textRenderer.draw(new MatrixStack(), Text.of(text), renderPos[0], renderPos[1], -1);

        if(renderer != null)
            renderer.render(mouseX, mouseY);

        super.render(new MatrixStack(), mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(renderer != null)
            renderer.mouseClick(mouseX, mouseY, button);

        if(button == 0 && isEnabling) {
            if(!UIUtil.basicCollisionCheck
                    (mouseX, mouseY,
                    enableX, enableY,
                    enableX + 100, enableY + 200
            )) {
                isEnabling = false;
            } else {
                int y = enableY + 20;
                int x = enableX + 5;

                for(HUDModule m : hudMods) {
                    if(m.toggled) continue;

                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, x - 5, y - 10, x + 95, y + 10)) {
                        m.toggle();

                        m.renderable.x = enableX;
                        m.renderable.y = enableY;

                        isEnabling = false;
                    }

                    y+=20;
                }
            }
        }

        if(button == 0 && UIUtil.basicCollisionCheck(mouseX, mouseY, width - 100, 5, width - 5, 25)) {
            isEnableOther = !isEnableOther;
        }

        for(HUDModule mod : hudMods) {
            if(!mod.toggled) continue;

            if(mod.renderable.basicCollisionCheck(mouseX, mouseY)) {
                if(button == 0) {
                    if(renderer != null) continue;
                    moving = mod.renderable;
                    moveX = (int) (mouseX - moving.x);
                    moveY = (int) (mouseY - moving.y);
                } else if(button == 1) {
                    renderer = new SettingsRenderer(0, 0, mod);
                    this.renderer.x = this.width - this.renderer.width - 10;
                    this.renderer.y = this.height - this.renderer.height - 10;
                }
            }

            if(renderer != null) continue;

            int x = mod.renderable.x;
            int y = mod.renderable.y + mod.renderable.height + 6;
            y+= (9 * 0.85) + 1;

            if(UIUtil.basicCollisionCheck(
                            mouseX, mouseY,
                            x, y,
                            mod.renderable.x + mod.renderable.width,
                            (int) (y + (textRenderer.fontHeight * 0.85F))
            )) {
                if(button == 0)
                    mod.toggle();
            }
        }

        if(button == 1 && renderer == null &&
                !UIUtil.basicCollisionCheck(mouseX, mouseY,
                        width - 125, 0,
                        width, otherArea
                )) {
            isEnabling = true;
            enableX = (int) mouseX;
            enableY = (int) mouseY;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(renderer != null)
            renderer.mouseRelease(mouseX, mouseY, button);

        if(button == 0)
            moving = null;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode != GLFW.GLFW_KEY_ESCAPE && renderer != null)
            renderer.onKeyboard(keyCode);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static HUDMoveScreen getCurrent() {
        return instance;
    }

    static class SettingsRenderer {
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

        public int width = 400;
        public int height = 300;

        public SettingsRenderer(double x, double y, Module mod) {
            this.x = x;
            this.y = y;
            this.mod = mod;
        }

        public void render(double mouseX, double mouseY) {
            UIUtil.drawRoundedRect(x, y, x + width, y + height, 20f, new Color(0, 0, 0, 84).getRGB());

            UIUtil.drawRoundedRect(x + width - 50, y + height - 25, x + width - 5, y + height - 5, 10f, new Color(255, 255, 255, 120).getRGB());
            int[] pos = UIUtil.getIdealRenderingPosForText("Close", x + width - 50, y + height - 25, x + width - 5, y + height - 5);

            tr.draw(
                    new MatrixStack(),
                    new LiteralText("Close")
                            .setStyle(
                                    Style
                                            .EMPTY
                                            .withUnderline(
                                                    UIUtil.basicCollisionCheck(
                                                            mouseX, mouseY,
                                                            x + width - 50, y + height - 25,
                                                            x + width - 5, y + height - 5
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
                    UIUtil.drawRoundedRect(x + width - 50, y, x + width - 2, y + 15, 5F, (bool.value ? new Color(255, 255, 255, 84) : new Color(0, 0, 0, 84)).getRGB());
                    tr.draw(new MatrixStack(),  bool.value ? "Enabled" : "Disabled", (float) (x + width - 48), y + 1, -1);
                }

                if(set instanceof ModeSetting mode) {
                    UIUtil.drawRoundedRect(x + width - 50, y, x + width - 2, y + 15, 5F, new Color(0, 0, 0, 84).getRGB());
                    tr.draw(new MatrixStack(), mode.value, (float) (x + width - 48), y + 1, -1);
                }

                if(set instanceof ColorSetting color) {
                    UIUtil.drawRoundedRect(x + width - 50, y, x + width - 2, y + 15, 5F, (setting == color ? new Color(255, 255, 255, 84) : new Color(0, 0, 0, 84)).getRGB());

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

                            UIUtil.drawRoundedRect(x + width - 232, y + 6, x + width - 32, y + 9, 1F, new Color(0, 0, 0, 84).getRGB());
                            tr.draw(new MatrixStack(), num.value + "", (float) (x + width - 28), y + 2, -1);

                            int renderX = (int) (x + width - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                            UIUtil.drawRoundedRect(renderX - 7, y, renderX + 7, y + 15, 15F, Color.BLUE.getRGB());

                            color.setColor(new Color(red, green, blue, alpha));
                        }
                        y+=10;
                    }
                }

                if(set instanceof NumberSetting num) {
                    UIUtil.drawRoundedRect(x + width - 232, y + 6, x + width - 32, y + 9, 1F, new Color(0, 0, 0, 84).getRGB());
                    tr.draw(new MatrixStack(), num.value + "", (float) (x + width - 28), y + 2, -1);

                    int renderX = (int) (x + width - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                    UIUtil.drawRoundedRect(renderX - 7, y, renderX + 7, y + 15, 15F, Color.BLUE.getRGB());
                }

                if(set instanceof KeyBindSetting key) {
                    UIUtil.drawRoundedRect(x + width - 60, y, x + width - 12, y + 15, 5F, new Color(0, 0, 0, 84).getRGB());
                    String kys = GLFW.glfwGetKeyName(key.key, 0);
                    if(settingBind)
                        kys = "Listening";

                    int[] poss = UIUtil.getIdealRenderingPosForText(kys, x + width - 60, y, x + width - 12, y + 15);
                    tr.draw(new MatrixStack(), kys, poss[0], poss[1], -1);
                }

                y+=20;
            }

            if(this.settingNumber) {
                double val = ((mouseX - holdX) - (x + width - 232));

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
                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + width - 50, currentY, x + width - 2, currentY + 15) && button == 0) {
                        bool.toggle();
                        BallSack.getInstance().config.save();
                    }
                }

                if(set instanceof ModeSetting mode) {
                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + width - 50, currentY, x + width - 2, currentY + 15) && button == 0) {
                        mode.toggle();
                        BallSack.getInstance().config.save();
                    }
                }

                if(set instanceof ColorSetting color) {
                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + width - 50, currentY, x + width - 2, currentY + 15) && button == 0) {
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

                            int renderX = (int) (x + width - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
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
                    int renderX = (int) (x + width - 232 + UIUtil.map(num.value, num.min, num.max, 0, 200));
                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, renderX - 7, currentY, renderX + 7, currentY + 15) && button == 0) {
                        this.settingNumber = true;
                        this.number = num;
                        this.holdX = (int) (mouseX - renderX);
                    }
                }

                if(set instanceof KeyBindSetting key) {
                    if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + width - 60, currentY, x + width - 12, currentY + 15) && button == 0) {
                        settingBind = true;
                        bindSetting = key;
                        BallSack.getInstance().config.save();
                    }
                }

                currentY+=20;
            }
        }

        public void mouseRelease(double mouseX, double mouseY, int button) {
            if(UIUtil.basicCollisionCheck(mouseX, mouseY, x + width - 50, y + height - 25, x + width - 5, y + height - 5) && button == 0) {
                HUDMoveScreen.getCurrent().renderer = null;
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

    static class ModuleEnabler extends CheckboxWidget {
        public Module mod;
        public HUDMoveScreen moveScreen;

        public ModuleEnabler(int x, int y, int width, int height, Module mod, HUDMoveScreen thiz) {
            super(x, y, width, height, Text.of(mod.name), mod.toggled);
            this.mod = mod;
            this.moveScreen = thiz;
        }

        @Override
        public void onPress() {
            if(!moveScreen.isEnableOther) return;

            this.mod.toggle();
            super.onPress();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if(moveScreen.isEnableOther)
                super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.moveScreen.isEnableOther && this.clicked(mouseX, mouseY) && button == 1) {
                this.moveScreen.renderer = new HUDMoveScreen.SettingsRenderer(0, 0, this.mod);

                this.moveScreen.renderer.x = this.moveScreen.width - this.moveScreen.renderer.width - 10;
                this.moveScreen.renderer.y = this.moveScreen.height - this.moveScreen.renderer.height - 10;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

}