package io.github.betterclient.client.util;

import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.ui.HUDMoveScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ModuleEnabler extends CheckboxWidget {
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
}
