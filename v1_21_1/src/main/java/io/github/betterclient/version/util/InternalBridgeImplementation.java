package io.github.betterclient.version.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class InternalBridgeImplementation implements IBridge.InternalBridge {
    public static Identifier lastText;

    @Override
    public void screen_renderBackground(IBridge.MatrixStack matrices) {
        MinecraftClient.getInstance().currentScreen.renderBackground(getContextForMatrices(matrices), 0, 0, 0);
    }

    @Override
    public IBridge.Text Text_literalText(String text) {
        IBridge.Text text1 = new IBridge.Text();
        text1.pointer = Text.literal(text);
        text1.str = text;
        text1.underline = false;
        return text1;
    }

    @Override
    public IBridge.Text Text_addStyle(IBridge.Text pointer, IBridge.Style s) {
        MutableText t = (MutableText) pointer.pointer;
        pointer.pointer = t.setStyle(Style.EMPTY.withUnderline(s.underline()));
        pointer.underline = s.underline();
        return pointer;
    }

    @Override
    public Object Identifier_new(String path) {
        if(path.contains(":"))
            return Identifier.of(path.split(":")[0], path.split(":")[1]);
        return Identifier.of("minecraft", path);
    }

    @Override
    public IBridge.ShaderEffect ShaderEffect_new(IBridge.Identifier shaderLocation) {
        try {
            return (IBridge.ShaderEffect) new PostEffectProcessor(
                    MinecraftClient.getInstance().getTextureManager(),
                    MinecraftClient.getInstance().getResourceManager(),
                    MinecraftClient.getInstance().getFramebuffer(),
                    (Identifier) shaderLocation.pointer
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void screen_fill(IBridge.MatrixStack matrices, int x, int y, int endX, int endY, int color) {
        getContextForMatrices(matrices).fill(x, y, endX, endY, color);
    }

    @Override
    public void drawTexture(IBridge.MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        getContextForMatrices(matrices).drawTexture(lastText, x, y, u, v, width, height, textureWidth, textureHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public Object Keybinding_new(IBridge.KeyBinding thiz, String translationKey, int code, String category) {
        return new KeyBinding(translationKey, code, category) {
            @Override
            public void setPressed(boolean pressed) {
                thiz.setPressed(pressed);
                super.setPressed(pressed);
            }
        };
    }

    @Override
    public void Keybinding_setKey(Object pointer, int key) {
        ((KeyBinding) pointer).setBoundKey(InputUtil.fromKeyCode(key, 0));
    }

    @Override
    public void GL11_disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void GL11_enableScissor(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        var res = MinecraftClient.getInstance().getWindow();
        x = (int) (x * res.getScaleFactor());
        height = (int) (height * res.getScaleFactor());
        y = (int) (res.getHeight() - (y * res.getScaleFactor()) - height);
        width = (int) (width * res.getScaleFactor());
        GL11.glScissor(x, y, width, height);
    }

    @Override
    public boolean isKeyPressed(int key) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);
    }

    @Override
    public String Identifier_namespace(Object pointer) {
        return ((Identifier) pointer).getNamespace();
    }

    @Override
    public String Identifier_path(Object pointer) {
        return ((Identifier) pointer).getPath();
    }

    public static DrawContext getContextForMatrices(IBridge.MatrixStack matrices) {
        return (DrawContext) matrices.getCTX();
    }
}
