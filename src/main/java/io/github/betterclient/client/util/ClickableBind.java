package io.github.betterclient.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class ClickableBind extends KeyBinding {
    public Runnable action;
    public Runnable unPress;
    public boolean before = false;

    public ClickableBind(String translationKey, int code, String category, Runnable action, Runnable onUnPress) {
        super(translationKey, code, category);

        try {
            Field f = KeyBinding.class.getDeclaredField("CATEGORY_ORDER_MAP");
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
            Field f = ops.getClass().getField("allKeys");

            f.set(ops, ArrayUtils.add(ops.allKeys, bind));
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
