package io.github.betterclient.client.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Renderable {
    public int x, y;
    public int width = 0, height = 0;
    public boolean renderBackground = false;
    public Runnable render = () -> {};
    public Color backgroundColor;
    public TextRenderer textRenderer;

    public Renderable(int x, int y) {
        this.x = x;
        this.y = y;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    public Renderable fillArea(int startX, int startY, int endX, int endY, Color color) {
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            DrawableHelper.fill(new MatrixStack(), this.getX() + startX, this.getY() + startY, this.getX() + endX, this.getY() + endY, color.getRGB());
        };

        if(endX > width) {
            width = endX;
        }
        if(endY > height) {
            height = endY;
        }

        return this;
    }

    public void render() {
        if(this.renderBackground) {
            DrawableHelper.fill(new MatrixStack(), x - 3, y - 3, x + width + 3, y + height + 3, this.backgroundColor.getRGB());
        }

        this.render.run();
    }

    public void renderWithXY(int x, int y) {
        int xx = this.x;
        int yy = this.y;

        this.x = x;
        this.y = y;

        if(this.renderBackground) {
            DrawableHelper.fill(new MatrixStack(), this.x, this.y, this.x + width, this.y + height, this.backgroundColor.getRGB());
        }

        this.render.run();

        this.x = xx;
        this.y = yy;
    }

    public Renderable renderText(String text, int x, int y, Color color) {
        int endX = x + this.textRenderer.getWidth(text);
        int endY = y + this.textRenderer.fontHeight;
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            textRenderer.draw(new MatrixStack(), text, this.getX() + x, this.getY() + y, color.getRGB());
        };

        if(endX > width) {
            width = endX;
        }
        if(endY > height) {
            height = endY;
        }

        return this;
    }

    public Renderable renderText(String text, int x, int y, Color color, float scale) {
        double endX = x + (this.textRenderer.getWidth(text) * scale);
        double endY = y + (this.textRenderer.fontHeight * scale);
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            RenderSystem.color4f(1, 1, 1, 1);
            MatrixStack matrices = new MatrixStack();
            matrices.push();
            matrices.translate(x,y,1);
            matrices.scale(scale,scale,1);
            matrices.translate(-x,-y,1);
            MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color.getRGB());
            matrices.pop();
        };

        if(endX > width) {
            width = (int) endX;
        }
        if(endY > height) {
            height = (int) endY;
        }

        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void reset() {
        this.render = () -> {};
        this.width = 0;
        this.height = 0;
    }

    public boolean basicCollisionCheck(double mouseX, double mouseY) {
        if (mouseX >= getX() & mouseX <= (getX() + width) & mouseY >= getY() & mouseY <= (getY() + height)) {
            return true;
        }else {
            return false;
        }
    }
}