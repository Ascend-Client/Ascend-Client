package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;

import java.awt.*;

public class KeyStrokesMod extends HUDModule {
    public boolean mouseKeys = true; //Make this a setting
    public Color backgroundColor = new Color(0,0,0,84); // Make this a setting too
    public Color backgroundColorPressed = new Color(150, 140, 140,156); // This one aswell
    public Color textColor = new Color(-1); //Also this...

    public KeyStrokesMod() {
        super("Keystrokes", 10, 10);
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

        renderable.fillArea(30, 0, 50, 20, w ? backgroundColorPressed : backgroundColor); //W
        renderable.fillArea(30, 22, 50, 42, s ? backgroundColorPressed : backgroundColor); //S
        renderable.fillArea(5, 22, 25, 42, a ? backgroundColorPressed : backgroundColor); //A
        renderable.fillArea(55, 22, 75, 42, d ? backgroundColorPressed : backgroundColor); //D

        renderable.renderText("W", renderable.getIdealRenderingPosForText("W", 30, 0, 50, 20), textColor);
        renderable.renderText("A", renderable.getIdealRenderingPosForText("A", 5, 22, 25, 42), textColor);
        renderable.renderText("S", renderable.getIdealRenderingPosForText("S", 30, 22, 50, 42), textColor);
        renderable.renderText("D", renderable.getIdealRenderingPosForText("D", 55, 22, 75, 42), textColor);

        if(mouseKeys){
            renderable.fillArea(5, 44, 38, 64, leftClick ? backgroundColorPressed : backgroundColor); //Left
            renderable.fillArea(42, 44, 75, 64, rightClick ? backgroundColorPressed : backgroundColor); //Right

            renderable.renderText("LMB", renderable.getIdealRenderingPosForText("LMB", 5, 44, 38, 64), textColor);
            renderable.renderText("RMB", renderable.getIdealRenderingPosForText("RMB", 42, 44, 75, 64), textColor);
        }
    }
}
