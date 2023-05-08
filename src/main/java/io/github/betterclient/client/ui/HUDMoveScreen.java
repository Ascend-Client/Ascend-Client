package io.github.betterclient.client.ui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.*;
import io.github.betterclient.client.mod.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class HUDMoveScreen extends Screen {
    public ModuleManager modMan = BallSack.getInstance().moduleManager;

    public Renderable moving = null;
    public int moveX = 0, moveY = 0;
    public List<HUDModule> hudMods = new Vector<>();

    public HUDMoveScreen() {
        super(Text.of(""));
        hudMods.addAll(modMan.getByCategory(Category.HUD).stream().map(HUDModule::cast).toList());
    }

    @Override
    protected void init() {
        moving = null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);

        if(moving != null) {
            moving.x = mouseX - moveX;
            moving.y = mouseY - moveY;
        }

        for(HUDModule mod : hudMods) {
            mod.render(new RenderEvent());
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(HUDModule mod : hudMods) {
            if(button == 0 && mod.renderable.basicCollisionCheck(mouseX, mouseY)) {
                moving = mod.renderable;
                moveX = (int) (mouseX - moving.x);
                moveY = (int) (mouseY - moving.y);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0)
            moving = null;

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
