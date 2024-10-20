package io.github.betterclient.client.ui.clickgui;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.ui.StringTypeUI;
import io.github.betterclient.client.util.StringTypeHandler;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClickGui extends Screen implements StringTypeHandler {
    /**
     * The current category
     * null if all modules are shown.
     */
    public Category current = null;

    private int w2, h2, c1, c2, c3, ce, cd;

    private int scrollY = 0;
    private int max = 0;
    private boolean isWaitingForString = false;
    private String currentConfig;

    @Override
    protected void init() {
        if(isWaitingForString) {
            isWaitingForString = false;
            Ascend.getInstance().config.switchConfig(currentConfig);
        }

        this.w2 = width / 2;
        this.h2 = height / 2;
        this.c1 = new Color(0, 0, 0, 84).getRGB();
        this.c2 = new Color(255, 255, 255, 180).getRGB();
        this.c3 = new Color(255, 255, 255, 130).getRGB();
        this.ce = new Color(0, 255, 0, 200).getRGB();
        this.cd = new Color(255, 0, 0, 200).getRGB();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(matrices);
        TextRenderer renderer = MinecraftClient.getInstance().getTextRenderer();

        UIUtil.drawRoundedRect(w2 - 200, h2 - 190, w2 + 200, h2 + 190, 10f, c1, IBridge.newMatrixStack());

        int w3 = renderer.bs$getWidth("Ascend Client");
        fill(matrices, w2 - 200, h2 - 168, w2 - 188 + w3, h2 - 180, c2);
        fill(matrices, w2 - 190, h2 - 180, w2 - 188 + w3, h2 - 190, c2);

        //1 sided round rect
        UIUtil.enableScissor(w2 - 200, h2 - 190, w2 - 190, h2 - 180);
        UIUtil.drawRoundedRect(w2 - 200, h2 - 190, w2 - 180, h2 - 170, 10f, c2, IBridge.newMatrixStack());
        UIUtil.disableScissor();

        float[] pos = UIUtil.getIdealRenderingPosForText("Ascend Client",
                w2 - 188 + w3, h2 - 190,
                w2 - 200, h2 - 170);
        renderer.drawWithShadow(matrices, "Ascend Client", pos[0], pos[1], -1);

        fill(matrices, w2 - 188 + w3, h2 - 170, w2 - 200, h2 - 168, -1);
        fill(matrices, w2 - 188 + w3, h2 - 190, w2 - 186 + w3, h2 + 190, -1);

        File[] configs = Arrays.stream(Objects.requireNonNullElse(Application.configFolder.listFiles(), new File[0])).filter(file -> file.getName().endsWith(".json")).toArray(File[]::new);
        int configPopulation = Math.min(configs.length, 7); //Allow a max of 7 configs to be shown.

        for(int i = 0; i < configPopulation; i++) {
            String configName = configs[i].getName().substring(0, configs[i].getName().lastIndexOf('.'));
            configName = UIUtil.capitalize(configName);

            float scale = (60f / renderer.bs$getWidth(configName));
            if(scale > 1)
                scale = 1;

            UIUtil.drawRoundedRect(w2 - 190, h2 + 170 - (i * 30), w2 - 198 + w3, h2 + 150 - (i * 30), 2f, c3, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText(configName, w2 - 190, h2 + 170 - (i * 30), w2 - 198 + w3, h2 + 150 - (i * 30), scale);
            float x = pos[0];
            float y = pos[1];

            matrices.bs$push();
            matrices.bs$translate(x,y,1);
            matrices.bs$scale(scale,scale,1);
            matrices.bs$translate(-x,-y,1);
            renderer.drawWithShadow(matrices, configName, x, y, -1);
            matrices.bs$pop();
        }

        int cx = w2 - 190, cy = h2 + 170 - (configPopulation * 30);
        matrices.bs$push();
        matrices.bs$translate(cx,cy,1);
        matrices.bs$scale(0.9f, 0.9f, 1f);
        matrices.bs$translate(-cx,-cy,1);
        renderer.drawWithShadow(matrices, "Configs", cx, cy, -1);
        matrices.bs$pop();

        int cex = (-198 + w3 - (-190)) / 2;
        UIUtil.drawRoundedRect(w2 - 190, h2 + 175, (w2 - 190) + cex - 5, h2 + 185, 2f, c3, IBridge.newMatrixStack());
        pos = UIUtil.getIdealRenderingPosForText("+", w2 - 190, h2 + 175, (w2 - 190) + cex - 5, h2 + 185);
        renderer.drawWithShadow(matrices, "+", pos[0], pos[1], -1);
        if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 190, h2 + 175, (w2 - 190) + cex - 5, h2 + 185)) {
            int csx = renderer.bs$getWidth("New Config");
            UIUtil.drawRoundedRect(mouseX, mouseY, mouseX + csx + 10, mouseY + 20, 2f, c1, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText("New Config", mouseX, mouseY, mouseX + csx + 10, mouseY + 20);
            renderer.draw(matrices, "New Config", pos[0], pos[1], -1);
        }

        UIUtil.drawRoundedRect((w2 - 190) + cex + 5, h2 + 175, w2 - 198 + w3, h2 + 185, 2f, c3, IBridge.newMatrixStack());
        pos = UIUtil.getIdealRenderingPosForText("-", (w2 - 190) + cex + 5, h2 + 175, w2 - 198 + w3, h2 + 185);
        renderer.drawWithShadow(matrices, "-", pos[0], pos[1], -1);
        if(UIUtil.basicCollisionCheck(mouseX, mouseY, (w2 - 190) + cex + 5, h2 + 175, w2 - 198 + w3, h2 + 185)) {
            int csx = renderer.bs$getWidth("Delete Config");
            UIUtil.drawRoundedRect(mouseX, mouseY, mouseX + csx + 10, mouseY + 20, 2f, c1, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText("Delete Config", mouseX, mouseY, mouseX + csx + 10, mouseY + 20);
            renderer.draw(matrices, "Delete Config", pos[0], pos[1], -1);
        }

        int pc = new Color(255, 255, 255, 100).getRGB();

        UIUtil.drawRoundedRect(w2 - 185, h2 - 160, w2 - 203 + w3, h2 - 145, 2f, current == null ? pc : c2, IBridge.newMatrixStack());
        pos = UIUtil.getIdealRenderingPosForText("All", w2 - 185, h2 - 160, w2 - 203 + w3, h2 - 145);
        renderer.draw(matrices, "All", pos[0], pos[1], -1);

        int y = 1;
        for (Category value : Category.values()) {
            UIUtil.drawRoundedRect(w2 - 185, h2 - 160 + (y * 20), w2 - 203 + w3, h2 - 145 + (y * 20), 2f, current == value ? pc : c2, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText(UIUtil.capitalize(value.name()), w2 - 185, h2 - 160 + (y * 20), w2 - 203 + w3, h2 - 145 + (y * 20));
            renderer.draw(matrices, UIUtil.capitalize(value.name()), pos[0], pos[1], -1);
            y++;
        }

        List<Module> modules;
        if(this.current == null) {
            modules = Ascend.getInstance().moduleManager.moduleList;
        } else {
            modules = Ascend.getInstance().moduleManager.getByCategory(current);
        }

        UIUtil.enableScissor(w2 - 200, h2 - 190, w2 + 200, h2 + 190);
        int i = 0;
        int latestY = 0;
        for (Module module : modules) {
            int px = (i % 3) * 90;
            int py = (i / 3) * 90 - scrollY;

            UIUtil.drawRoundedRect(w2 - 100 + px, h2 - 170 + py, w2 - 20 + px, h2 - 90 + py, 10f, c3, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText(module.name, w2 - 100 + px, h2 - 120 + py, w2 - 20 + px, h2 - 110 + py);
            renderer.draw(matrices, module.name, pos[0], pos[1], -1);

            UIUtil.drawRoundedRect(w2 - 90 + px, h2 - 95 + py, w2 - 30 + px, h2 - 105 + py, 2f, module.toggled ? ce : cd, IBridge.newMatrixStack());
            pos = UIUtil.getIdealRenderingPosForText(module.toggled ? "Enabled" : "Disabled", w2 - 90 + px, h2 - 95 + py, w2 - 30 + px, h2 - 105 + py);
            renderer.draw(matrices, module.toggled ? "Enabled" : "Disabled", pos[0], pos[1], -1);

            if(module.icon == null) {
                UIUtil.drawRoundedRect(w2 - 90 + px, h2 - 160 + py, w2 - 30 + px, h2 - 125 + py, 2f, -1, IBridge.newMatrixStack());
            } else {
                UIUtil.drawRoundedRect(w2 - 90 + px, h2 - 160 + py, w2 - 30 + px, h2 - 125 + py, 0f, c1, IBridge.newMatrixStack());
                MinecraftClient.getInstance().setShaderTexture(0, module.icon);
                MinecraftClient.getInstance().setShaderColor(1, 1, 1, 0.7f);
                drawTexture(matrices, w2 - 90 + px, h2 - 160 + py, 0, 0, 60, 35, 60, 35);
            }

            latestY = h2 - 90 + (i / 3) * 90;
            i++;
        }
        UIUtil.disableScissor();

        max = ((i / 3) * 90) - h2 - 20;
        if(latestY <= h2 + 190) {
            scrollY = 0;
            max = 0;
        }

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 200, h2 - 190, w2 + 200, h2 + 190)) {
            int newValue;
            if(amount == -1) {
                newValue = scrollY + 10;
                if (newValue > max) {
                    newValue = max;
                }
            } else {
                newValue = scrollY - 10;
                if (newValue < 0) {
                    newValue = 0;
                }
            }
            scrollY = newValue;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextRenderer renderer = MinecraftClient.getInstance().getTextRenderer();
        int w3 = renderer.bs$getWidth("Ascend Client");

        if(button == 0) {
            if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 185, h2 - 160, w2 - 203 + w3, h2 - 145)) {
                current = null;
                scrollY = 0;
                max = 0;
            }

            int y = 1;
            for (Category value : Category.values()) {
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 185, h2 - 160 + (y * 20), w2 - 203 + w3, h2 - 145 + (y * 20))) {
                    current = value;
                    scrollY = 0;
                    max = 0;
                }
                y++;
            }

            File[] configs = Arrays.stream(Objects.requireNonNullElse(Application.configFolder.listFiles(), new File[0])).filter(file -> file.getName().endsWith(".json")).toArray(File[]::new);
            int configPopulation = Math.min(configs.length, 7);

            for(int i = 0; i < configPopulation; i++) {
                String configName = configs[i].getName().substring(0, configs[i].getName().lastIndexOf('.'));

                if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 190, h2 + 170 - (i * 30), w2 - 198 + w3, h2 + 150 - (i * 30)))
                    Ascend.getInstance().config.switchConfig(configName);
            }

            int cex = (-198 + w3 - (-190)) / 2;
            if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 190, h2 + 175, (w2 - 190) + cex - 5, h2 + 185)) {
                isWaitingForString = true;
                MinecraftClient.getInstance().setGuiScreen(new StringTypeUI(this));
            }

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, (w2 - 190) + cex + 5, h2 + 175, w2 - 198 + w3, h2 + 185)) {
                if(configs.length != 1) {
                    if(!Ascend.getInstance().config.loadedConfig.delete()) {
                        System.out.println("Failed to delete (?)");
                    }

                    String configName = Arrays.stream(Objects.requireNonNullElse(Application.configFolder.listFiles(), new File[0])).filter(file -> file.getName().endsWith(".json")).toList().get(0).getName().replace(".json", "");
                    Ascend.getInstance().config.switchConfig(configName);
                }
            }
        }

        List<Module> modules;
        if(this.current == null) {
            modules = Ascend.getInstance().moduleManager.moduleList;
        } else {
            modules = Ascend.getInstance().moduleManager.getByCategory(current);
        }

        if(mouseY > h2 + 190) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int i = 0;
        for (Module module : modules) {
            int px = (i % 3) * 90;
            int py = (i / 3) * 90 - scrollY;

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 100 + px, h2 - 170 + py, w2 - 20 + px, h2 - 90 + py) && button == 0) {
                Ascend.getInstance().config.save();
                module.toggle();
            }

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, w2 - 100 + px, h2 - 170 + py, w2 - 20 + px, h2 - 90 + py) && button == 1) {
                MinecraftClient.getInstance().setGuiScreen(new SettingsGui(module));
            }

            i++;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setCurrentConfig(String cfg) {
        this.currentConfig = cfg;
    }

    @Override
    public void isNotWaiting() {
        isWaitingForString = false;
    }
}
