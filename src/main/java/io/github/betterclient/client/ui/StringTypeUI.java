package io.github.betterclient.client.ui;

import io.github.betterclient.client.ui.clickgui.OtherModsUI;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class StringTypeUI extends Screen {
    public String text = "";
    public OtherModsUI ui;

    public StringTypeUI(OtherModsUI ui) {
        super(Text.of(""));
        this.ui = ui;
    }

    @Override
    protected void init() {
        ButtonWidget widget = ButtonWidget.builder(Text.literal("Accept"), button -> {
            ui.currentConfig = text;
            MinecraftClient.getInstance().setScreen(ui);
        }).dimensions(width / 2 - 50, height / 2 + 30, 98, 20).build();
        this.addDrawableChild(widget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        UIUtil.drawRoundedRect(width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25, 5f, new Color(0, 0, 0, 81).getRGB());
        int[] pos = UIUtil.getIdealRenderingPosForText(text, width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25);
        textRenderer.draw(new MatrixStack(), text, pos[0], pos[1] + 10, -1);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if(keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if(keyCode == GLFW.GLFW_KEY_ENTER) {
                ui.currentConfig = text;
                MinecraftClient.getInstance().setScreen(ui);
            } else if(keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if(!text.equals(""))
                    this.text = this.text.substring(0, this.text.length() - 1);
            } else {
                if(chr != '\0') {
                    this.text += chr;
                }
            }
        }

        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if(keyCode == GLFW.GLFW_KEY_ENTER) {
                ui.currentConfig = text;
                MinecraftClient.getInstance().setScreen(ui);
            } else if(keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if(!text.equals(""))
                    this.text = this.text.substring(0, this.text.length() - 1);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}