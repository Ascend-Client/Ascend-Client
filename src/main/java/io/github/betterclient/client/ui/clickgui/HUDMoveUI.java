package io.github.betterclient.client.ui.clickgui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.*;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;
import java.util.List;
import java.util.Vector;

public class HUDMoveUI extends Screen {
    public ModuleManager modMan = BallSack.getInstance().moduleManager;

    public Renderable moving = null;
    public int moveX = 0, moveY = 0;
    public List<HUDModule> hudMods = new Vector<>();

    public boolean isEnabling = false;
    public int enableX = 0, enableY = 0;
    public boolean isDropDown = false;
    public int dropdownx, dropdowny;

    public boolean isSize = false;
    public int sizeX, sizeStartX, sizeEndX;
    public HUDModule sizeMod = null;

    public HUDMoveUI() {
        super();
        hudMods.addAll(modMan.getByCategory(Category.HUD).stream().map(HUDModule::cast).toList());
    }

    @Override
    protected void init() {
        moving = null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        for(HUDModule mod : hudMods) {
            if(!mod.toggled) continue;

            Renderable render = mod.renderable;

            UIUtil.drawRoundedRect(
                    render.x - 4, render.y - 4,
                    render.x + render.width + 4,
                    render.y + render.height + 4,
                    10f, new Color(0, 0, 0, 84).getRGB(),
                    matrices);

            if(mod.isSizeable()) {
                UIUtil.drawRoundedRect(render.x + render.width, render.y + render.height,
                        render.x + render.width + 6, render.y + render.height + 6,
                        2f, Color.BLUE.getRGB(), matrices);
            }

            mod.render(new RenderEvent());
        }

        if(isDropDown) {
            UIUtil.drawRoundedRect(
                    dropdownx, dropdowny,
                    dropdownx + 100, dropdowny + 70,
                    20f, new Color(0, 0, 0, 150).getRGB(),
                    matrices);

            HUDModule dropDownMod = null;
            for(HUDModule mod : hudMods) {
                if(!mod.toggled) continue;

                if(UIUtil.basicCollisionCheck(dropdownx, dropdowny, mod.renderable.x, mod.renderable.y, mod.renderable.x + mod.renderable.width, mod.renderable.y + mod.renderable.height)) {
                    dropDownMod = mod;
                    break;
                }
            }

            if(dropDownMod != null)
                textRenderer.draw(matrices, dropDownMod.name, dropdownx + 4, dropdowny + 10, -1);

            textRenderer.draw(matrices, Text.literal("Settings").withStyle(Style.withUnderline(UIUtil.basicCollisionCheck(mouseX, mouseY, dropdownx, dropdowny + 20, dropdownx + 100, dropdowny + 40))), dropdownx + 4, dropdowny + 30, -1);
            textRenderer.draw(matrices, Text.literal("Disable").withStyle(Style.withUnderline(UIUtil.basicCollisionCheck(mouseX, mouseY, dropdownx, dropdowny + 40, dropdownx + 100, dropdowny + 60))), dropdownx + 4, dropdowny + 50, -1);
        }

        if(isEnabling) {
            UIUtil.drawRoundedRect(
                    enableX, enableY,
                    enableX + 100, enableY + 200,
                    20f, new Color(0, 0, 0, 150).getRGB(),
                   matrices);

            int y = enableY + 10;
            int x = enableX + 5;

            for(HUDModule m : hudMods) {
                if(m.toggled) continue;

                boolean withStyle = false;
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x - 5, y - 10, x + 95, y + 10)) {
                    UIUtil.drawRoundedRect(x - 5, y - 6, x + 95, y + 14, 20f,
                            new Color(0, 0, 0, 150).getRGB(), matrices);
                    withStyle = true;
                }

                textRenderer.draw(matrices,
                        Text.literal(m.name).withStyle(Style.withUnderline(withStyle)),
                         x, y, -1
                );

                y+=20;
            }
        }

        if(isSize) {
            sizeMod.size.value = mouseX - sizeStartX;
            if(sizeMod.size.value > 200) {
               sizeMod.size.value = 200;
            } else if(sizeMod.size.value < 50) {
                sizeMod.size.value = 50;
            }
        }

        UIUtil.drawRoundedRect(width / 2f - 40, height / 2f - 55, width / 2f + 40, height / 2f - 30,
                5F, new Color(0, 0, 0, 120).getRGB(), matrices);

        String text = "Enable Mods";

        float[] renderPos = UIUtil.getIdealRenderingPosForText(text, width / 2f - 40, height / 2f - 55, width / 2f + 40, height / 2f - 30);

        textRenderer.draw(matrices, text, renderPos[0], renderPos[1], -1);

        if(moving != null) {
            int finalX = mouseX - moveX;
            int finalY = mouseY - moveY;

            for(HUDModule mod : hudMods) {
                if (!mod.toggled) continue;
                if (mod.renderable.equals(moving)) continue;

                Renderable rend = mod.renderable;

                if(mouseX >= rend.x - 5 && mouseX <= (rend.x + rend.width + 5) && Math.abs(mouseY - rend.y) < 150) {
                    boolean renderPlusHeight = rend.y < moving.y;

                    fill(matrices, rend.x - 1, rend.y + 2 + (renderPlusHeight ? rend.height : 0), rend.x + 1, moving.y - 2 + (renderPlusHeight ? 0 : rend.height), -1);

                    finalX = rend.x;
                    break;
                }

                if(mouseY >= rend.y - 5 && mouseY <= (rend.y + rend.height + 5) && Math.abs(mouseX - rend.x) < 150) {
                    boolean renderPlusWidth = rend.x < moving.x;

                    fill(matrices, rend.x + (renderPlusWidth ? rend.width : 0), rend.y + rend.height - (rend.height / 2) - 1, moving.x + (renderPlusWidth ? 0 : moving.width), rend.y + rend.height - (rend.height / 2) + 1, -1);

                    finalY = rend.y;
                    break;
                }
            }

            moving.x = finalX;
            moving.y = finalY;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

        if(button == 0 && isDropDown) {
            HUDModule dropDownMod = null;
            for(HUDModule mod : hudMods) {
                if(!mod.toggled) continue;

                if(UIUtil.basicCollisionCheck(dropdownx, dropdowny, mod.renderable.x, mod.renderable.y, mod.renderable.x + mod.renderable.width, mod.renderable.y + mod.renderable.height)) {
                    dropDownMod = mod;
                    break;
                }
            }

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, dropdownx, dropdowny + 20, dropdownx + 100, dropdowny + 40)) {
                MinecraftClient.getInstance().setGuiScreen(new SettingsGui(dropDownMod));
                isDropDown = false;
            }

            if(UIUtil.basicCollisionCheck(mouseX, mouseY, dropdownx, dropdowny + 40, dropdownx + 100, dropdowny + 60)) {
                if(dropDownMod != null) dropDownMod.toggle();
                isDropDown = false;
            }
        }

        if(button == 0 && UIUtil.basicCollisionCheck(mouseX, mouseY, width / 2f - 40, height / 2f - 55, width / 2f + 40, height / 2f - 30) && !isDropDown) {
            MinecraftClient.getInstance().setGuiScreen(new ClickGui());
        }

        for(HUDModule mod : hudMods) {
            if(!mod.toggled) continue;

            if(mod.renderable.basicCollisionCheck(mouseX, mouseY)) {
                if(button == 0) {
                    moving = mod.renderable;
                    moveX = (int) (mouseX - moving.x);
                    moveY = (int) (mouseY - moving.y);
                    isDropDown = false;
                } else if(button == 1) {
                    isDropDown = true;
                    dropdownx = (int) mouseX;
                    dropdowny = (int) mouseY;
                    isEnabling = false;

                    return super.mouseClicked(mouseX, mouseY, button);
                }
            } else {
                if(button == 0) {
                    isDropDown = false;
                }
            }

            Renderable render = mod.renderable;
            if(UIUtil.basicCollisionCheck(mouseX, mouseY, render.x + render.width, render.y + render.height,
                    render.x + render.width + 6, render.y + render.height + 6) && mod.isSizeable()) {
                isSize = true;
                isDropDown = false;
                isEnabling = false;
                sizeX = (int) mouseX;

                sizeStartX = sizeX - mod.size.value;
                sizeEndX = sizeStartX + mod.size.max;

                sizeMod = mod;
            }
        }

        if(button == 1) {
            isEnabling = true;
            isDropDown = false;
            enableX = (int) mouseX;
            enableY = (int) mouseY;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0 && moving != null) {
            moving = null;
            BallSack.getInstance().config.save();
        }

        isSize = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }
}