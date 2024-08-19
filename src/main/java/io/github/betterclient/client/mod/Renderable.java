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
    public float size = 1;
    public MatrixStack matrices;
    public boolean update = false;

    public Renderable(int x, int y, HUDModule owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.textRenderer = MinecraftClient.getInstance().getTextRenderer();
    }

    public Renderable drawCircle(float x, float y, float halfRadius, Color color) {
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            UIUtil.renderCircle(this.getX() + x, this.getY() + y, halfRadius, color.getRGB(), matrices);
        };

        if((x + halfRadius) > width) {
            width = (int) (x + halfRadius);
        }
        if((y + halfRadius) > height) {
            height = (int) (y + halfRadius);
        }

        return this;
    }

    public Renderable fillArea(int startX, int startY, int endX, int endY, Color color) {
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            UIUtil.drawRoundedRect(this.getX() + startX, this.getY() + startY, this.getX() + endX, this.getY() + endY, 5f, color.getRGB(), matrices);
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
        if(update) {
            update = false;
            this.width *= this.size;
            this.height *= this.size;
        }
        matrices = IBridge.newMatrixStack();
        if(size != 1) {
            matrices.bs$push();
            matrices.bs$translate(getX(), getY(), 0);
            matrices.bs$scale(size, size, 1);
            matrices.bs$translate(-getX(), -getY(), 0);
        }
        if(this.renderBackground) {
            UIUtil.drawRoundedRect(this.x - 4, this.y - 4, this.x + oldWidth + 4, this.y + oldHeight + 4, 10f, this.backgroundColor.getRGB(), matrices);
        }

        this.render.run();

        if(size != 1) {
            matrices.bs$pop();
        }
    }

    public void renderWithXY(int x, int y) {
        if(update) {
            update = false;
            this.width *= this.size;
            this.height *= this.size;
            this.oldWidth *= this.size;
            this.oldHeight *= this.size;
        }
        matrices = IBridge.newMatrixStack();
        if(size != 1) {
            matrices.bs$push();
            matrices.bs$translate(x, y, 0);
            matrices.bs$scale(size, size, 1);
            matrices.bs$translate(-x, -y, 0);
        }
        int xx = this.x;
        int yy = this.y;

        this.x = x;
        this.y = y;

        if(this.renderBackground) {
            UIUtil.drawRoundedRect(this.x - 4, this.y - 4, this.x + oldWidth + 4, this.y + oldHeight + 4, 10f, this.backgroundColor.getRGB(), matrices);
        }

        this.reset();
        this.owner.render(this);
        this.render.run();

        this.x = xx;
        this.y = yy;
        if(size != 1) {
            matrices.bs$pop();
        }
    }

    public Renderable renderText(String text, int x, int y, Color color) {
        int endX = x + this.textRenderer.bs$getWidth(text);
        int endY = y + this.textRenderer.fontHeight();
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            textRenderer.draw(matrices, text, this.getX() + x, this.getY() + y, color.getRGB());
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
        double endX = x + (this.textRenderer.bs$getWidth(text) * scale);
        double endY = y + (this.textRenderer.fontHeight() * scale);
        Runnable rrrr = render;
        render = () -> {
            rrrr.run();
            IBridge.getInstance().getClient().emptyShaderColor();
            matrices.bs$push();
            matrices.bs$translate(x,y,1);
            matrices.bs$scale(scale,scale,1);
            matrices.bs$translate(-x,-y,1);
            this.textRenderer.draw(matrices, text, x, y, color.getRGB());
            matrices.bs$pop();
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
        this.update = true;
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

        int textWidth = this.textRenderer.bs$getWidth(text);
        int textHeight = this.textRenderer.fontHeight();

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

                UIUtil.drawRoundedRect(this.getX() + x, this.getY() + endY, this.getX() + x + (float) (renderDamage), this.getY() + endY + 2, 1, damageColor.getRGB(), matrices);
                endY += 6;
            }

            MinecraftClient.getInstance().renderInGui(matrices, is, this.getX() + x, this.getY() + y);

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

            if (is.stackable) { //Render Amount
                String text = count + "";
                this.textRenderer.draw(matrices, text, this.getX() + endX - 3, this.getY() + endY - 3, color.getRGB());
                float[] width = new float[] {this.textRenderer.bs$getWidth(text), this.textRenderer.fontHeight()};

                endX = (int) ((endX - 3) + width[0]);
                endY = (int) ((endY - 3) + width[1]);
            }

            MinecraftClient.getInstance().renderInGui(matrices, is, this.getX() + x, this.getY() + y);

            if (endX > width) {
                width = endX;
            }
            if (endY > height) {
                height = endY;
            }
        };

        return this;
    }

    public Renderable renderEntity(Entity entity, int x, int y, int size, float mouseX, float mouseY) {
        Runnable oldRender = render;
        render = () -> {
            oldRender.run();
            int endX = x + (size * 2);
            int endY = y + (size * 2);
            int rx = x + getX() + (size);
            int ry = y + getY() + (size * 2);

            IBridge.MinecraftClient.getInstance().renderEntityInGUI(entity, rx, ry, size, mouseX, mouseY);

            if (endX > width) {
                width = endX;
            }
            if (endY > height) {
                height = endY;
            }
        };

        return this;
    }

    public void setSize(float v) {
        if(this.size != v) {
            this.size = v;
            this.width *= v;
            this.height *= v;
            this.oldWidth *= v;
            this.oldHeight *= v;
        }
    }
}