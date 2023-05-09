package io.github.betterclient.client.ui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.*;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.Vector;

public class HUDMoveScreen extends Screen {
    public ModuleManager modMan = BallSack.getInstance().moduleManager;

    public Renderable moving = null;
    public int moveX = 0, moveY = 0;
    public List<HUDModule> hudMods = new Vector<>();

    public boolean isEnabling = false;
    public int enableX = 0, enableY = 0;


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
            if(!mod.toggled) continue;

            Renderable render = mod.renderable;

            UIUtil.drawRoundedRect(
                    render.x - 4, render.y - 4,
                    render.x + render.width + 4,
                    render.y + render.height + 4,
                    10f, new Color(0, 0, 0, 84).getRGB()
            );

            matrices = new MatrixStack();

            matrices.push();
            int x = render.x;
            int y = render.y + render.height + 6;

            matrices.translate(x,y,1);
            matrices.scale(0.85f,0.85f,1);
            matrices.translate(-x,-y,1);
            textRenderer.draw(matrices, mod.name, x, y, -1);
            matrices.pop();

            float scale = 0.85f;

            x = (int) (x + render.width - (textRenderer.getWidth("Disable") * scale)) + 5;

            boolean underLine = UIUtil.basicCollisionCheck(
                    mouseX, mouseY,
                    x, y,
                    render.x + render.width, (int) (y + (textRenderer.fontHeight * scale))
            );

            Style style = Style.EMPTY.withUnderline(underLine);

            matrices.translate(x,y,1);
            matrices.scale(scale,scale,1);
            matrices.translate(-x,-y,1);
            textRenderer.draw(matrices, new LiteralText("Disable").setStyle(style), x, y, Color.RED.getRGB());
            matrices.pop();

            mod.render(new RenderEvent());
        }

        if(isEnabling) {
            UIUtil.drawRoundedRect(
                    enableX, enableY,
                    enableX + 100, enableY + 200,
                    20f, new Color(0, 0, 0, 150).getRGB()
            );

            int y = enableY + 10;
            int x = enableX + 5;

            for(HUDModule m : hudMods) {
                if(m.toggled) continue;

                boolean withStyle = false;
                if(UIUtil.basicCollisionCheck(mouseX, mouseY, x - 5, y - 10, x + 95, y + 10)) {
                    UIUtil.drawRoundedRect(x - 5, y - 6, x + 95, y + 14, 20f,
                            new Color(0, 0, 0, 150).getRGB());
                    withStyle = true;
                }

                textRenderer.draw(new MatrixStack(),
                        new LiteralText(m.name).setStyle(Style.EMPTY.withUnderline(withStyle)),
                         x, y, -1
                );

                y+=20;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
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

        for(HUDModule mod : hudMods) {
            if(!mod.toggled) continue;

            if(button == 0 && mod.renderable.basicCollisionCheck(mouseX, mouseY)) {
                moving = mod.renderable;
                moveX = (int) (mouseX - moving.x);
                moveY = (int) (mouseY - moving.y);
            }

            int y = mod.renderable.y + mod.renderable.height + 6;
            int startX = (mod.renderable.x + mod.renderable.width) - 5;

            if(button == 0 &&
                    UIUtil.basicCollisionCheck(
                            mouseX, mouseY,
                            (int) (startX - (textRenderer.getWidth("Disable") * 0.85)), y,
                            startX, (int) (y + (textRenderer.fontHeight * 0.85))
                    )
            ) {
                mod.toggle();
            }
        }

        if(button == 1) {
            isEnabling = true;
            enableX = (int) mouseX;
            enableY = (int) mouseY;
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
