package io.github.betterclient.client.util;

import io.github.betterclient.client.ui.HUDMoveScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ClickableBind extends KeyBinding {
    public Runnable action;

    public ClickableBind(String translationKey, int code, String category, Runnable action) {
        super(translationKey, code, category);

        try {
            Field f = KeyBinding.class.getDeclaredField("categoryOrderMap");
            f.setAccessible(true);
            Map<String, Integer> ff = (Map<String, Integer>) f.get(null);

            if(!ff.containsKey(category)) {
                ff.put(category, 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.action = action;
    }

    public static void registerClientKeybinds() {
        try {
            GameOptions ops = MinecraftClient.getInstance().options;
            Field f = ops.getClass().getField("keysAll");

            f.set(ops, ArrayUtils.addAll(ops.keysAll, getBinds()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyBinding[] getBinds() {
        List<KeyBinding> binds = new Vector<>();

        binds.add(new ClickableBind("Open ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, "BallSack Client", () -> {
            MinecraftClient.getInstance().openScreen(new HUDMoveScreen());
        }));

        return binds.toArray(new KeyBinding[0]);
    }

    @Override
    public void setPressed(boolean pressed) {
        if(pressed)
            this.action.run();

        super.setPressed(pressed);
    }
}
