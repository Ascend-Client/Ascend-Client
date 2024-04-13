package io.github.betterclient.version.mixin.bridge;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.ui.clickgui.HUDMoveUI;
import io.github.betterclient.version.util.ItemsImplementation;
import io.github.betterclient.version.util.ScreenLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IBridge.MinecraftClient {

    @Shadow public abstract void openScreen(@Nullable Screen screen);

    @Shadow @Final public TextRenderer textRenderer;

    @Shadow @Final public InGameHud inGameHud;

    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow private static int currentFps;

    @Shadow @Final public Mouse mouse;

    @Shadow @Final public GameOptions options;

    @Shadow @Final private Window window;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow public abstract TextureManager getTextureManager();

    @Override
    public void setGuiScreen(IBridge.Screen ui) {
        this.openScreen(new ScreenLoader(ui));
    }

    @Override
    public IBridge.MatrixStack newMatrixStack() {
        return (IBridge.MatrixStack) new MatrixStack();
    }

    @Override
    public IBridge.TextRenderer getTextRenderer() {
        return (IBridge.TextRenderer) this.textRenderer;
    }

    @Override
    public void addMessage(IBridge.Text literal) {
        this.inGameHud.getChatHud().addMessage((Text) literal.pointer);
    }

    @Override
    public IBridge.RaycastResult raycast(IBridge.Entity entity, IBridge.Vec3d camera, IBridge.Vec3d hits, IBridge.BoundingBox box, int id, double d) {
        EntityHitResult hitResult = ProjectileUtil.raycast((Entity) entity, new Vec3d(camera.x, camera.y, camera.z), new Vec3d(hits.x, hits.y, hits.z), new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), entity1 -> entity1.getEntityId() == id, d);

        return hitResult == null ? null : new IBridge.RaycastResult((IBridge.Entity) hitResult.getEntity(), new IBridge.Vec3d(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z));
    }

    @Override
    public void setShaderColor(float red, float green, float blue, float alpha) {
        RenderSystem.color4f(red, green, blue, alpha);
    }

    @Override
    public void renderInGui(IBridge.MatrixStack matrixStack, IBridge.ItemStack is, int x, int y) {
        GL11.glEnable(GL11.GL_BLEND);

        this.itemRenderer.renderInGui((ItemStack) is.pointer, x, y);

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public IBridge.PlayerEntity getPlayer() {
        return (IBridge.PlayerEntity) this.player;
    }

    @Override
    public IBridge.Items getItems() {
        return ItemsImplementation.get();
    }

    @Override
    public int getFPS() {
        return currentFps;
    }

    @Override
    public IBridge.GameOptions getOptions() {
        return (IBridge.GameOptions) this.options;
    }

    @Override
    public Object getCurrentServerEntry() {
        return ((MinecraftClient) (Object) this).getCurrentServerEntry();
    }

    @Override
    public int getPing() {
        return (int) ((MinecraftClient) (Object) this).getCurrentServerEntry().ping;
    }

    @Override
    public String getAddress() {
        return ((MinecraftClient) (Object) this).getCurrentServerEntry().address;
    }

    @Override
    public IBridge.Window getWindow() {
        return IBridge.Window.class.cast(this.window);
    }

    @Override
    public boolean isKeyPressed(int key) {
        return InputUtil.isKeyPressed(this.window.getHandle(), key);
    }

    @Override
    public Object getCurrentScreenPointer() {
        return this.currentScreen;
    }

    @Override
    public String getKeyName(int key, int scancode) {
        return GLFW.glfwGetKeyName(key, scancode);
    }

    @Override
    public IBridge.BufferBuilder getBufferBuilder() {
        return (IBridge.BufferBuilder) Tessellator.getInstance().getBuffer();
    }

    @Override
    public void openNonCustomScreen(IBridge.NonCustomScreen screen) {
        Screen toOpen = new ScreenLoader(new HUDMoveUI());
        switch (screen) {
            case SINGLEPLAYER -> toOpen = new SelectWorldScreen(this.currentScreen);
            case MULTIPLAYER -> toOpen = new MultiplayerScreen(this.currentScreen);
            case OPTIONS -> toOpen = new OptionsScreen(this.currentScreen, this.options);
        }
        this.openScreen(toOpen);
    }

    @Override
    public void setShaderTexture(int texNum, IBridge.Identifier identifier) {
        this.getTextureManager().bindTexture((Identifier) identifier.pointer);
    }

    @Override
    public IBridge.Mouse getMouse() {
        return (IBridge.Mouse) mouse;
    }

    @Override
    public void renderCurrentScreen(IBridge.MatrixStack matrices, int x, int y, float delta) {
        this.currentScreen.render((MatrixStack) matrices, x, y, delta);
    }

    @Override
    public boolean isCustomScreen(Object screen) {
        return screen instanceof ScreenLoader;
    }

    @Override
    public void openNonCustomScreen(Object screen) {
        this.openScreen((Screen) screen);
    }
}
