package io.github.betterclient.client.mod;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.UIUtil;
import io.github.betterclient.client.bridge.IBridge.*;

import java.awt.*;

public class Renderable {
    public int x, y;
    public int width = 0, height = 0;
    public int oldWidth = 0, oldHeight = 0;
    public boolean renderBackground = false;
    public Runnable render = () -> {};
    public Color backgroundColor;
    public TextRenderer textRenderer;
    public final HUDModule owner;

    public Renderable(int x, int y, HUDModule owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.textRenderer = MinecraftClient.getInstance().getTextRenderer();
    }

    public Renderable fillArea(int startX, int startY, int endX, int endY, Color color) {
        this.preRender();

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
            UIUtil.drawRoundedRect(this.x - 4, this.y - 4, this.x + oldWidth + 4, this.y + oldHeight + 4, 10f, this.backgroundColor.getRGB());
        }

        this.render.run();
    }

    public void renderWithXY(int x, int y) {
        int xx = this.x;
        int yy = this.y;

        this.x = x;
        this.y = y;

        if(this.renderBackground) {
            UIUtil.drawRoundedRect(this.x - 4, this.y - 4, this.x + oldWidth + 4, this.y + oldHeight + 4, 10f, this.backgroundColor.getRGB());
        }

        this.reset();
        this.owner.render(this);
        this.render.run();

        this.x = xx;
        this.y = yy;
    }

    public Renderable renderText(String text, int x, int y, Color color) {
        this.preRender();

        int endX = x + this.textRenderer.getWidth(text);
        int endY = y + this.textRenderer.fontHeight();
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            textRenderer.draw(IBridge.newMatrixStack(), text, this.getX() + x, this.getY() + y, color.getRGB());
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
        this.preRender();

        double endX = x + (this.textRenderer.getWidth(text) * scale);
        double endY = y + (this.textRenderer.fontHeight() * scale);
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            IBridge.getInstance().getClient().emptyShaderColor();
            MatrixStack matrices = IBridge.newMatrixStack();
            matrices.push();
            matrices.translate(x,y,1);
            matrices.scale(scale,scale,1);
            matrices.translate(-x,-y,1);
            MinecraftClient.getInstance().getTextRenderer().draw(matrices, text, x, y, color.getRGB());
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
        this.preRender();

        int[] pos = new int[2];

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        int textWidth = minecraftClient.getTextRenderer().getWidth(text);
        int textHeight = minecraftClient.getTextRenderer().fontHeight();

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
        this.preRender();

        Runnable oldRender = render;
        render = () -> {
            oldRender.run();
            int endX = x + 16;
            int endY = y + 16;

            if (is.itemDamagable && renderText) {
                double damage = ((is.maxDamage - is.damage) / (double) is.maxDamage) * 100;
                double renderDamage = ((is.maxDamage - is.damage) / (double) is.maxDamage) * 16;
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

            MinecraftClient.getInstance().renderInGui(IBridge.newMatrixStack(), is, this.getX() + x, this.getY() + y);

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
        this.preRender();

        Runnable oldRender = render;
        render = () -> {
            oldRender.run();
            int endX = x + 16;
            int endY = y + 16;

            if (is.stackable) { //Render Amount
                String text = count + "";
                this.textRenderer.draw(IBridge.newMatrixStack(), text, this.getX() + endX - 3, this.getY() + endY - 3, color.getRGB());
                float[] width = new float[] {this.textRenderer.getWidth(text), this.textRenderer.fontHeight()};

                endX = (int) ((endX - 3) + width[0]);
                endY = (int) ((endY - 3) + width[1]);
            }

            MinecraftClient.getInstance().renderInGui(IBridge.newMatrixStack(), is, this.getX() + x, this.getY() + y);

            if (endX > width) {
                width = endX;
            }
            if (endY > height) {
                height = endY;
            }
        };

        return this;
    }

    private void preRender() {
        this.textRenderer = MinecraftClient.getInstance().getTextRenderer();
    }
}