package io.github.betterclient.client.ui;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.File;

public class OtherModsUI extends Screen {
    public Mode currentMode = Mode.MOD_ENABLE;
    public String currentConfig = "";
    public boolean isWaitingForString = false;

    public OtherModsUI() {
        super(Text.of(""));
    }

    @Override
    protected void init() {
        if(isWaitingForString) {
            isWaitingForString = false;

            BallSack.getInstance().config.switchConfig(currentConfig);
        }

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        UIUtil.drawRoundedRect(width / 2 - 200, height / 2 - 200, width / 2 + 200, height / 2 + 200, 10f, new Color(0, 0, 0, 81).getRGB());

        fill(new MatrixStack(), width / 2 - 200, height / 2 - 177, width / 2 + 200, height / 2 - 180, -1);

        boolean enMods = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 - 190, height / 2 - 200, width / 2 - 190 + 10 + (textRenderer.getWidth("Enable Mods")),height / 2 - 180);
        boolean config = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 - 120, height / 2 - 200, width / 2 - 120 + 10 + (textRenderer.getWidth("Config")),height / 2 - 180);
        boolean back = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 + 190 + (-10 - (textRenderer.getWidth("Go Back"))), height / 2 - 200, width / 2 + 190,height / 2 - 180);

        textRenderer.draw(new MatrixStack(), new LiteralText("Enable Mods").setStyle(Style.EMPTY.withUnderline(enMods)),width / 2 - 190, height / 2 - 191, -1);
        textRenderer.draw(new MatrixStack(), new LiteralText("Config").setStyle(Style.EMPTY.withUnderline(config)),width / 2 - 120, height / 2 - 191, -1);
        textRenderer.draw(new MatrixStack(), new LiteralText("Go Back").setStyle(Style.EMPTY.withUnderline(back)), width / 2 + 190 - (textRenderer.getWidth("Go Back")), height / 2 - 191, -1);

        if(currentMode == Mode.MOD_ENABLE) {
            int cy = height / 2 - 170;
            int index = 0;
            int start = width / 2 - 190;

            for (Module mod : BallSack.getInstance().moduleManager.getByCategory(Category.OTHER)) {
                if(index == 5) {
                    index = 0;
                    cy += 25;
                }

                fill(new MatrixStack(), start + (index * 77), cy, start + ((index) * 77) + 72, cy + 20, new Color(0, 0, 0, 81).getRGB());
                float scale = (72f / textRenderer.getWidth(mod.name));
                if(scale > 1)
                    scale = 1;

                int[] pos = UIUtil.getIdealRenderingPosForText(mod.name, start + (index * 77), cy, start + ((index) * 77) + 72, cy + 20, scale);

                int x = pos[0];
                int y = pos[1];

                MatrixStack pose = new MatrixStack();

                pose.push();
                pose.translate(x,y,1);
                pose.scale(scale,scale,1);
                pose.translate(-x,-y,1);
                textRenderer.draw(pose, mod.name, x, y, -1);
                pose.pop();

                y += 7;
                x = start + (index * 77) + 2;
                pose.push();
                pose.translate(x, y, 1);
                pose.scale(0.7f, 0.7f, 1f);
                pose.translate(-x, -y, 1);
                textRenderer.draw(pose, mod.toggled ? "Enabled" : "Disabled", x, y, (mod.toggled ? Color.GREEN : Color.RED).getRGB());
                pose.pop();


                index++;
            }
        } else {
            int cy = height / 2 - 150;
            int cx = width / 2 - 190;

            int[] ideal = UIUtil.getIdealRenderingPosForText("+", cx, height / 2 - 170, cx + 16, height / 2 - 155);
            UIUtil.drawRoundedRect(cx, height / 2 - 170, cx + 16, height / 2 - 155, 2f, -1); //+
            textRenderer.draw(new MatrixStack(), "+", ideal[0], ideal[1], Color.black.getRGB());

            ideal = UIUtil.getIdealRenderingPosForText("Load", cx + 24, height / 2 - 170, cx + 64, height / 2 - 155);
            UIUtil.drawRoundedRect(cx + 24, height / 2 - 170, cx + 64, height / 2 - 155, 2f, -1); //Load
            textRenderer.draw(new MatrixStack(), "Load", ideal[0], ideal[1], Color.black.getRGB());

            UIUtil.drawRoundedRect(cx - 4, cy - 2, cx + 380, cy + ((Application.configFolder.listFiles().length - 1) * 10) + 2, 5f, new Color(0, 0, 0, 81).getRGB());

            for(File cfg : Application.configFolder.listFiles()) {
                if(cfg.getName().contains(".txt")) continue;

                boolean isHover = UIUtil.basicCollisionCheck(mouseX, mouseY, cx, cy, cx + (textRenderer.getWidth(cfg.getName())) + 2, cy + 10);

                String text = cfg.getName().replace(".json", "");
                String selected = (cfg.getName().equals(currentConfig) ? " - Selected" : "");
                String current = (BallSack.getInstance().config.loadedConfig.getName().equals(cfg.getName()) ? (selected.equals("") ? " - Current" : ", Current") : "");
                textRenderer.draw(new MatrixStack(), new LiteralText(text + selected + current).setStyle(Style.EMPTY.withUnderline(isHover)), cx, cy, -1);
                cy+=10;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean enMods = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 - 190, height / 2 - 200, width / 2 - 190 + 10 + (textRenderer.getWidth("Enable Mods")),height / 2 - 180);
        boolean config = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 - 120, height / 2 - 200, width / 2 - 120 + 10 + (textRenderer.getWidth("Config")),height / 2 - 180);
        boolean back = UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2 + 190 + (-10 - (textRenderer.getWidth("Go Back"))), height / 2 - 200, width / 2 + 190,height / 2 - 180);

        if(button == 0) {
            if(enMods) currentMode = Mode.MOD_ENABLE;
            if(config) currentMode = Mode.CONFIG;
            if(back) MinecraftClient.getInstance().openScreen(new HUDMoveUI());
        }

        if(currentMode == Mode.MOD_ENABLE) {
            int cy = height / 2 - 170;
            int index = 0;
            int start = width / 2 - 190;

            for (Module mod : BallSack.getInstance().moduleManager.getByCategory(Category.OTHER)) {
                if (index == 5) {
                    index = 0;
                    cy += 25;
                }

                if (UIUtil.basicCollisionCheck(mouseX, mouseY, start + (index * 77), cy, start + ((index) * 77) + 72, cy + 20)) {
                    if(button == 0) {
                        mod.toggle();
                    }
                    if(button == 1) {
                        MinecraftClient.getInstance().openScreen(new SettingsUI(mod));
                    }
                }

                index++;
            }
        } else {
            int cy = height / 2 - 150;
            int cx = width / 2 - 190;

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, cx, height / 2 - 170, cx + 16, height / 2 - 155) && button == 0) {
                isWaitingForString = true;
                MinecraftClient.getInstance().openScreen(new StringTypeUI(this));
            }

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, cx + 24, height / 2 - 170, cx + 64, height / 2 - 155) && !currentConfig.equals("") && button == 0) {
                BallSack.getInstance().config.switchConfig(currentConfig.replace(".json", ""));
            }

            for(File cfg : Application.configFolder.listFiles()) {
                if(cfg.getName().contains(".txt")) continue;

                boolean isHover = UIUtil.basicCollisionCheck(mouseX, mouseY, cx, cy, cx + (textRenderer.getWidth(cfg.getName())) + 2, cy + 10);

                if(isHover && button == 0) {
                    currentConfig = cfg.getName();
                }

                cy+=10;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    enum Mode {
        MOD_ENABLE,
        CONFIG
    }
}