package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.ColorSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;

import java.awt.*;

public class KeyStrokesMod extends HUDModule {
    public BooleanSetting mouseKeys = new BooleanSetting("Mouse Keys", true);
    public ColorSetting backgroundColorPressed = new ColorSetting("Background Color (Pressed)", new Color(150, 140, 140,156)); // This one aswell

    public KeyStrokesMod() {
        super("Keystrokes", 10, 10);
        this.addSetting(mouseKeys);
        this.addSetting(backgroundColorPressed);
    }

    @Override
    public void render(Renderable renderable) {
        boolean leftClick = MinecraftClient.getInstance().mouse.wasLeftButtonClicked();
        boolean rightClick = MinecraftClient.getInstance().mouse.wasRightButtonClicked();
        GameOptions op = MinecraftClient.getInstance().options;

        boolean w = op.keyForward.isPressed();
        boolean s = op.keyBack.isPressed();
        boolean d = op.keyRight.isPressed();
        boolean a = op.keyLeft.isPressed();

        renderable.fillArea(25, 0, 45, 20, w ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //W
        renderable.fillArea(25, 22, 45, 42, s ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //S
        renderable.fillArea(0, 22, 20, 42, a ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //A
        renderable.fillArea(50, 22, 70, 42, d ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //D

        renderable.renderText("W", renderable.getIdealRenderingPosForText("W", 25, 0, 45, 20), textColor.getColor());
        renderable.renderText("A", renderable.getIdealRenderingPosForText("A", 0, 22, 20, 42), textColor.getColor());
        renderable.renderText("S", renderable.getIdealRenderingPosForText("S", 25, 22, 45, 42), textColor.getColor());
        renderable.renderText("D", renderable.getIdealRenderingPosForText("D", 50, 22, 70, 42), textColor.getColor());

        if(mouseKeys.isValue()){
            renderable.fillArea(0, 44, 32, 64, leftClick ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //Left
            renderable.fillArea(38, 44, 70, 64, rightClick ? backgroundColorPressed.getColor() : backgroundColor.getColor()); //Right

            renderable.renderText("LMB", renderable.getIdealRenderingPosForText("LMB", 0, 44, 32, 64), textColor.getColor());
            renderable.renderText("RMB", renderable.getIdealRenderingPosForText("RMB", 38, 44, 70, 64), textColor.getColor());
        }
    }
}
