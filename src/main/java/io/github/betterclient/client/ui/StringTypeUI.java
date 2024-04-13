package io.github.betterclient.client.ui;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.ui.clickgui.OtherModsUI;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;

public class StringTypeUI extends IBridge.Screen {
    public String text = "";
    public OtherModsUI ui;

    public StringTypeUI(OtherModsUI ui) {
        super();
        this.ui = ui;
    }

    @Override
    protected void init() {
        IBridge.ButtonWidget widget = new IBridge.ButtonWidget(IBridge.Text.literal("Accept"), () -> {
            ui.currentConfig = text;
            IBridge.getInstance().getClient().setGuiScreen(ui);
        }).dimensions(width / 2 - 50, height / 2 + 30, 98, 20);
        this.addButton(widget);
    }

    @Override
    public void render(IBridge.MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        UIUtil.drawRoundedRect(width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25, 5f, new Color(0, 0, 0, 81).getRGB());
        int[] pos = UIUtil.getIdealRenderingPosForText(text, width / 2 - 50, height / 2 - 25, width / 2 + 50, height / 2 + 25);
        textRenderer.draw(IBridge.newMatrixStack(), text, pos[0], pos[1] + 10, -1);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if(keyCode != IBridge.getKeys().KEY_ESCAPE) {
            if(keyCode == IBridge.getKeys().KEY_ENTER) {
                ui.currentConfig = text;
                IBridge.MinecraftClient.getInstance().setGuiScreen(ui);
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
                ui.currentConfig = text;
                IBridge.MinecraftClient.getInstance().setGuiScreen(ui);
            } else if(keyCode == IBridge.getKeys().KEY_BACKSPACE) {
                if(!text.equals(""))
                    this.text = this.text.substring(0, this.text.length() - 1);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}