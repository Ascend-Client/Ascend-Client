package io.github.betterclient.client.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Renderable {
    public int x, y;
    public int width = 0, height = 0;
    public int oldWidth = 0, oldHeight = 0;
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
            UIUtil.drawRoundedRect(this.getX() + startX, this.getY() + startY, this.getX() + endX, this.getY() + endY, 5f, color.getRGB());
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
            UIUtil.drawRoundedRect(this.x, this.y, this.x + oldWidth, this.y + oldHeight, 5f, this.backgroundColor.getRGB());
        }

        this.render.run();
    }

    public void renderWithXY(int x, int y) {
        int xx = this.x;
        int yy = this.y;

        this.x = x;
        this.y = y;

        if(this.renderBackground) {
            UIUtil.drawRoundedRect(this.x, this.y, this.x + oldWidth, this.y + oldHeight, 5f, this.backgroundColor.getRGB());
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
        this.oldHeight = height;
        this.oldWidth = width;
        this.width = 0;
        this.height = 0;
    }

    public boolean basicCollisionCheck(double mouseX, double mouseY) {
        return UIUtil.basicCollisionCheck(
                mouseX, mouseY,
                this.getX(), this.getY(),
                this.getX() + width, this.getY() + height
        );
    }

    public int[] getIdealRenderingPosForText(String text, int x, int y, int endX, int endY) {
        int[] pos = new int[2];

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        int textWidth = minecraftClient.textRenderer.getWidth(text);
        int textHeight = minecraftClient.textRenderer.fontHeight;

        int idealX = x + ((endX - x) / 2) - (textWidth / 2);
        pos[0] = Math.max(x, Math.min(idealX, endX - textWidth));

        int idealY = y + ((endY - y) / 2) - (textHeight / 2);
        pos[1] = Math.max(y, Math.min(idealY, endY - textHeight));

        return pos;
    }

    public void renderText(String text, int[] pos, Color color) {
        this.renderText(text, pos[0], pos[1], color);
    }

    public Renderable renderItemStack(int x, int y, ItemStack is, boolean renderText) {
        Runnable oldRender = render;
        render = () -> {
            oldRender.run();
            int endX = x + 16;
            int endY = y + 16;

            if (is.getItem().isDamageable() && renderText) {
                double damage = ((is.getMaxDamage() - is.getDamage()) / (double) is.getMaxDamage()) * 100;
                double renderDamage = ((is.getMaxDamage() - is.getDamage()) / (double) is.getMaxDamage()) * 16;
                Color damageColor;

                if (damage >= 25 && damage <= 70) {
                    damageColor = new Color(255, 255, 0);
                } else if (damage > 70) {
                    damageColor = new Color(0, 255, 0);
                } else {
                    damageColor = new Color(255, 0, 0);
                }

                UIUtil.drawRoundedRect(this.getX() + x, this.getY() + endY, this.getX() + x + (float) (renderDamage), this.getY() + endY + 2, 1, damageColor.getRGB());
                endY += 6;
            }

            GL11.glEnable(GL11.GL_BLEND);

            MinecraftClient.getInstance().getItemRenderer().renderInGui(is, this.getX() + x, this.getY() + y);

            GL11.glDisable(GL11.GL_BLEND);

            if (endX > width) {
                width = endX;
            }
            if (endY > height) {
                height = endY;
            }
        };

        return this;
    }

    public Renderable renderItemStack(int x, int y, ItemStack is, int count, Color color) {
        Runnable oldRender = render;
        render = () -> {
            oldRender.run();
            int endX = x + 16;
            int endY = y + 16;

            if (is.isStackable()) { //Render Amount
                String text = count + "";
                this.textRenderer.draw(new MatrixStack(), text, this.getX() + endX - 3, this.getY() + endY - 3, color.getRGB());
                float[] width = new float[] {this.textRenderer.getWidth(text), this.textRenderer.fontHeight};

                endX = (int) ((endX - 3) + width[0]);
                endY = (int) ((endY - 3) + width[1]);
            }

            RenderSystem.enableBlend();
            MinecraftClient.getInstance().getItemRenderer().renderInGui(is, this.getX() + x, this.getY() + y);
            RenderSystem.disableBlend();

            if (endX > width) {
                width = endX;
            }
            if (endY > height) {
                height = endY;
            }
        };

        return this;
    }
}