package io.github.betterclient.client.bridge;

import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public interface IBridge {
    static IBridge getInstance() {
        try {
            return (IBridge) Class.forName("io.github.betterclient.version.Version").getDeclaredField("bridge").get(null);
        } catch (Exception e) {
            throw new RuntimeException("hi (youll never see me)");
        }
    }

    static PreLaunchBridge getPreLaunch() {
        try {
            return (PreLaunchBridge) Class.forName("io.github.betterclient.version.Version").getDeclaredField("preLaunchBridge").get(null);
        } catch (Exception e) {
            throw new RuntimeException("hi (youll never see me)");
        }
    }

    static KeyStorage getKeys() {
        return getInstance().getKeyStorage();
    }
    static InternalBridge internal() {
        return getInstance().getInternal();
    }
    static MatrixStack newMatrixStack() {
        return getInstance().getClient().newMatrixStack();
    }
    static ShaderEffect newShaderEffect(Identifier shaderLocation) {return internal().ShaderEffect_new(shaderLocation);}

    KeyStorage getKeyStorage();
    InternalBridge getInternal();

    interface PreLaunchBridge {
        MinecraftVersion getVersion();
        void info(String s);
        void error(String s);
        List<File> getVersionMods();
        void registerVersionBallsackMods(ModuleManager manager);
    }

    interface InternalBridge {
        void screen_renderBackground(MatrixStack matrices);

        Text Text_literalText(String text);

        Text Text_addStyle(Text pointer, Style s);

        Object Identifier_new(String path);

        ShaderEffect ShaderEffect_new(Identifier shaderLocation);

        void screen_fill(MatrixStack matrices, int x, int y, int endX, int endY, int color);
        void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight);

        Object Keybinding_new(KeyBinding thiz, String translationKey, int code, String category);

        void Keybinding_setKey(Object pointer, int key);

        void GL11_disableScissor();

        void GL11_enableScissor(int x, int y, int width, int height);

        boolean isKeyPressed(int key);

        String Identifier_namespace(Object pointer);

        String Identifier_path(Object pointer);
    }

    String getVersion();
    MinecraftClient getClient();

    enum Perspective {
        FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT
    }

    interface MinecraftClient {
        static MinecraftClient getInstance() {
            return IBridge.getInstance().getClient();
        }

        void setGuiScreen(Screen ui);

        MatrixStack newMatrixStack();

        TextRenderer getTextRenderer();
        TextRenderer getMCRenderer();

        void addMessage(Text literal);

        RaycastResult raycast(Entity entity, Vec3d camera, Vec3d hits, BoundingBox box, int id, double d);

        default void emptyShaderColor() {
            setShaderColor(1, 1, 1, 1);
        }

        void setShaderColor(float red, float green, float blue, float alpha);

        void renderInGui(MatrixStack matrixStack, ItemStack is, int x, int y);

        PlayerEntity getPlayer();

        Items getItems();

        int getFPS();

        GameOptions getOptions();

        Object getCurrentServerEntry();

        int getPing();

        String getAddress();

        Window getWindow();

        boolean isKeyPressed(int key);

        Object getCurrentScreenPointer();

        String getKeyName(int key, int scancode);

        BufferBuilder getBufferBuilder();

        void openNonCustomScreen(NonCustomScreen screen);

        void scheduleStop();

        void setShaderTexture(int texNum, Identifier identifier);

        Mouse getMouse();

        void renderCurrentScreen(MatrixStack matrices, int x, int y, float delta);

        boolean isCustomScreen(Object screen);

        void openNonCustomScreen(Object screen);

        boolean isChat(Object screen);
    }

    enum NonCustomScreen {
        SINGLEPLAYER,
        MULTIPLAYER,
        OPTIONS
    }

    enum BeginMode {
        LINES,
        LINE_STRIP,
        DEBUG_LINES,
        DEBUG_LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        QUADS;
    }

    interface BufferBuilder {

        void begin(BeginMode triangleFan);

        void vertex(MatrixStack matrices, float x, float y, float z, int color);

        void draw();
    }

    interface ShaderEffect {
        void setupDimensions(int width, int height);

        List<ShaderPass> getPasses();

        void close();
    }

    interface ShaderPass {
        void setUniformByName(String uniformName, float num);
    }

    interface Window {
        int width();
        int height();
        int scaledWidth();

        int scaledHeight();

        boolean isFocused();
    }

    interface Mouse {
        int getX();
        int getY();
        boolean wasRightButtonClicked();
        boolean wasLeftButtonClicked();
    }

    class Items {
        public final ItemStack DIAMOND_HELMET;
        public final ItemStack DIAMOND_CHESTPLATE;
        public final ItemStack DIAMOND_LEGGINGS;
        public final ItemStack DIAMOND_BOOTS;

        public Items(ItemStack diamondHelmet, ItemStack diamondChestplate, ItemStack diamondLeggings, ItemStack diamondBoots) {
            DIAMOND_HELMET = diamondHelmet;
            DIAMOND_CHESTPLATE = diamondChestplate;
            DIAMOND_LEGGINGS = diamondLeggings;
            DIAMOND_BOOTS = diamondBoots;
        }
    }

    class Screen {
        public List<ButtonWidget> buttons = new ArrayList<>();
        public int width, height;
        public TextRenderer textRenderer;

        public static boolean hasShiftDown() {return IBridge.internal().isKeyPressed(IBridge.getKeys().KEY_SHIFT);}

        protected void init() {}
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {}
        public boolean mouseClicked(double mouseX, double mouseY, int button) {return true;}
        public boolean charTyped(char chr, int keyCode) {return true;}
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {return true;}
        public boolean mouseReleased(double mouseX, double mouseY, int button) {return true;}
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {return true;}
        public boolean shouldCloseOnEsc() {return true;}

        protected void addButton(ButtonWidget widget) {
            buttons.add(widget);
        }
        protected void renderBackground(MatrixStack matrices) { IBridge.internal().screen_renderBackground(matrices); }
        protected void fill(MatrixStack matrices, int x, int y, int endX, int endY, int color) { IBridge.internal().screen_fill(matrices, x, y, endX, endY, color); }
        protected void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) { IBridge.internal().drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight); }

        public void initWrapper() {
            init();
        }
    }

    class ButtonWidget {
        public int x, y, width, height;
        public Text text;
        public Runnable onClick;

        public ButtonWidget(Text text, Runnable onClick) {
            this.text = text;
            this.onClick = onClick;
        }

        public ButtonWidget dimensions(int i, int i1, int i2, int i3) {
            this.x = i;
            this.y = i1;
            this.width = i2;
            this.height = i3;
            return this;
        }
    }

    class Text {
        public Object pointer;
        public boolean underline;
        public String str;

        public static Text literal(String s) {
            return IBridge.internal().Text_literalText(s);
        }

        public Text withStyle(Style s) {
            return IBridge.internal().Text_addStyle(this, s);
        }
    }

    interface TextRenderer {
        void draw(MatrixStack matrices, String text, float x, float y, int color);
        void draw(MatrixStack matrices, Text text, float x, float y, int color);

        int getWidth(String text);
        int fontHeight();

        void drawWithShadow(MatrixStack matrices, String text, float x, float y, int color);
    }

    interface MatrixStack {
        default void setCTX(Object o) {}
        default Object getCTX() {
            return null;
        }
        void push();
        void pop();

        void translate(float x, float y, float z);

        void scale(float scaleX, float scaleY, float scaleZ);
    }

    class KeyStorage {
        public final int KEY_SHIFT;
        public final int KEY_C;
        public final int KEY_RSHIFT;
        public final int KEY_BACKSPACE;
        public final int KEY_ESCAPE;
        public final int KEY_ENTER;
        public final int KEY_ALT;

        public KeyStorage(int keyShift, int keyC, int keyRshift, int keyBackspace, int keyEscape, int keyEnter, int keyAlt) {
            KEY_SHIFT = keyShift;
            KEY_C = keyC;
            KEY_RSHIFT = keyRshift;
            KEY_BACKSPACE = keyBackspace;
            KEY_ESCAPE = keyEscape;
            KEY_ENTER = keyEnter;
            KEY_ALT = keyAlt;
        }
    }

    interface CommandDispatcher {
        void register(String commandName, CommandExecutor executor, CommandArgument... arguments);
    }

    interface GameOptions {
        boolean forwardPressed();
        boolean backPressed();
        boolean rightPressed();
        boolean leftPressed();

        Perspective getPerspective();

        void setPerspective(Perspective thirdPersonBack);
        double getGamma();
        void setGamma(double v);

        void setSmoothCameraEnabled(boolean smoothCamera);

        boolean isSmoothCamera();

        void addBind(KeyBinding bind);
    }

    interface CommandExecutor {
        int execute(List<?> arguments);
    }

    class Style {
        boolean underline;

        public static Style withUnderline(boolean outline) {
            Style style = new Style();
            style.underline = outline;
            return style;
        }

        public boolean underline() {
            return underline;
        }
    }

    record CommandArgument(String name, Class<?> clazz) { }

    class Vec3d {
        public final double x, y, z;

        public Vec3d(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distanceTo(Vec3d vec) {
            double deltaX = vec.x - this.x;
            double deltaY = vec.y - this.y;
            double deltaZ = vec.z - this.z;

            return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        }

        public Vec3d add(double x, double y, double z) {
            return new Vec3d(this.x + x, this.y + y, this.z + z);
        }

        public Vec3d multiply(double value) {
            return new Vec3d(this.x * value, this.y * value, this.z * value);
        }
    }

    class BoundingBox {
        public final double minX, minY, minZ, maxX, maxY, maxZ;

        public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public BoundingBox stretch(Vec3d vec3d) {
            double d = this.minX;
            double e = this.minY;
            double f = this.minZ;
            double g = this.maxX;
            double h = this.maxY;
            double i = this.maxZ;
            if (vec3d.x < 0.0) {
                d += vec3d.x;
            } else if (vec3d.x > 0.0) {
                g += vec3d.x;
            }

            if (vec3d.y < 0.0) {
                e += vec3d.y;
            } else if (vec3d.y > 0.0) {
                h += vec3d.y;
            }

            if (vec3d.z < 0.0) {
                f += vec3d.z;
            } else if (vec3d.z > 0.0) {
                i += vec3d.z;
            }

            return new BoundingBox(d, e, f, g, h, i);
        }

        public BoundingBox expand(double x, double y, double z) {
            double d = this.minX - x;
            double e = this.minY - y;
            double f = this.minZ - z;
            double g = this.maxX + x;
            double h = this.maxY + y;
            double i = this.maxZ + z;
            return new BoundingBox(d, e, f, g, h, i);
        }
    }

    interface Entity {
        Vec3d getCameraPosVec(int number);
        Vec3d getRotationVec(int number);
        Vec3d getPos();
        BoundingBox getBox();
        int getID();
    }

    interface PlayerEntity extends Entity {
        ItemStack getArmorStack(int num);

        String getBiome();
        String getFacing();
    }

    class RaycastResult {
        public final Entity entity;
        public final Vec3d pos;

        public RaycastResult(Entity entity, Vec3d pos) {
            this.entity = entity;
            this.pos = pos;
        }
    }

    class ItemStack {
        public final Object pointer;
        public final boolean itemDamagable;
        public final int damage, maxDamage;
        public final boolean stackable;

        public ItemStack(Object pointer, boolean itemDamagable, int damage, int maxDamage, boolean stackable) {
            this.pointer = pointer;
            this.itemDamagable = itemDamagable;
            this.damage = damage;
            this.maxDamage = maxDamage;
            this.stackable = stackable;
        }
    }

    class Resource {
        public final ResourceSupplier resourceSupplier;

        public Resource(ResourceSupplier resourceSupplier) {
            this.resourceSupplier = resourceSupplier;
        }

        public static InputStream toInputStream(String str, String charset) {
            try {
                return new ByteArrayInputStream(str.getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    interface ResourceSupplier {
        InputStream getInputStream();
    }

    class Identifier {
        public final String path;
        public final Object pointer;

        public Identifier(Object pointer) {
            this.path = "";
            this.pointer = pointer;
        }

        public Identifier(String path) {
            this.path = path;
            this.pointer = getInstance().getInternal().Identifier_new(path);
        }

        public String path() {
            return getInstance().getInternal().Identifier_path(pointer);
        }

        public String namespace() {
            return getInstance().getInternal().Identifier_namespace(pointer);
        }
    }

    class KeyBinding {
        public Object pointer;

        public KeyBinding(String translationKey, int code, String category) {
            this.pointer = IBridge.internal().Keybinding_new(this, translationKey, code, category);
        }

        public void setPressed(boolean bool) {}
        public void setKey(int key) { IBridge.internal().Keybinding_setKey(pointer, key); }
    }
}
