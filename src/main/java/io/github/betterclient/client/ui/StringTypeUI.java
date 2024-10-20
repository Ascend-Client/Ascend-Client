package io.github.betterclient.client.ui;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.StringTypeHandler;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;

public class StringTypeUI extends IBridge.Screen {
    public String text = "";
    public StringTypeHandler ui;

    public StringTypeUI(StringTypeHandler ui) {
        super();
        this.ui = ui;
    }

    @Override
    protected void init() {
        IBridge.ButtonWidget widget = new IBridge.ButtonWidget(IBridge.Text.literal("Accept"), () -> {
            if (text.isEmpty()) {
                ui.isNotWaiting();
                IBridge.getInstance().getClient().setGuiScreen((IBridge.Screen) ui);
                return;
            }

            ui.setCurrentConfig(text);
            IBridge.getInstance().getClient().setGuiScreen((IBridge.Screen) ui);
        }).dimensions(width / 2 - 50, height / 2 + 30, 98, 20);
        this.addButton(widget);
    }

    @Override
    public void render(IBridge.MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        UIUtil.drawRoundedRect(width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25, 5f, new Color(0, 0, 0, 81).getRGB(), IBridge.newMatrixStack());
        float[] pos = UIUtil.getIdealRenderingPosForText(text, width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25);
        textRenderer.draw(IBridge.newMatrixStack(), text, pos[0], pos[1] + 10, -1);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if(keyCode != IBridge.getKeys().KEY_ESCAPE) {
            if(keyCode == IBridge.getKeys().KEY_ENTER) {
                ui.setCurrentConfig(text);
                IBridge.MinecraftClient.getInstance().setGuiScreen((IBridge.Screen) ui);
            } else if(keyCode == IBridge.getKeys().KEY_BACKSPACE) {
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
        if(keyCode != IBridge.getKeys().KEY_ESCAPE) {
            if(keyCode == IBridge.getKeys().KEY_ENTER) {
                ui.setCurrentConfig(text);
                IBridge.MinecraftClient.getInstance().setGuiScreen((IBridge.Screen) ui);
            } else if(keyCode == IBridge.getKeys().KEY_BACKSPACE) {
                if(!text.equals(""))
                    this.text = this.text.substring(0, this.text.length() - 1);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}