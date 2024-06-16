package io.github.betterclient.version.util;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.impl.other.SmoothFont;
import io.github.betterclient.client.util.smooth.SmoothTextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ScreenLoader extends Screen {
    public IBridge.Screen ui;

    public ScreenLoader(IBridge.Screen ui) {
        super(Text.of(""));
        this.ui = ui;
    }

    @Override
    protected void init() {
        ui.width = width;
        ui.height = height;
        ui.buttons.clear();
        ui.initWrapper();

        for (IBridge.ButtonWidget button : ui.buttons) {
            this.addButton(
                    new ButtonWidget(button.x, button.y, button.width, button.height, (Text) button.text.pointer, button1 -> button.onClick.run())
            );
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ui.textRenderer = SmoothFont.instance.toggled ? SmoothTextRenderer.instance : (IBridge.TextRenderer) this.textRenderer;
        ui.render((IBridge.MatrixStack) matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ui.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ui.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        ui.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ui.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        ui.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return ui.shouldCloseOnEsc();
    }
}
