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
    public Runnable unPress;
    public boolean before = false;

    public ClickableBind(String translationKey, int code, String category, Runnable action, Runnable onUnPress) {
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
        this.unPress = onUnPress;
    }

    public static ClickableBind registerKeyBind(ClickableBind bind) {
        try {
            GameOptions ops = MinecraftClient.getInstance().options;
            Field f = ops.getClass().getField("keysAll");

            f.set(ops, ArrayUtils.add(ops.keysAll, bind));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bind;
    }

    @Override
    public void setPressed(boolean pressed) {
        if(pressed) {
            if(!before) {
                this.action.run();
            }
            before = true;
        } else {
            if(before) {
                this.unPress.run();
            }
            before = false;
        }

        super.setPressed(pressed);
    }
}
