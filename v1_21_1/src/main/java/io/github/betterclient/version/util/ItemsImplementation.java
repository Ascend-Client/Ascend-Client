package io.github.betterclient.version.util;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemsImplementation extends IBridge.Items {
    private static ItemsImplementation yea;

    public ItemsImplementation() {
        super(convert(new ItemStack(Items.DIAMOND_HELMET)), convert(new ItemStack(Items.DIAMOND_CHESTPLATE)), convert(new ItemStack(Items.DIAMOND_LEGGINGS)), convert(new ItemStack(Items.DIAMOND_BOOTS)));
    }

    private static IBridge.ItemStack convert(ItemStack stack) {
        return new IBridge.ItemStack(
                stack,
                stack.isDamageable(),
                stack.getDamage(),
                stack.getMaxDamage(),
                stack.isStackable()
        );
    }

    public static ItemsImplementation get() {
        return yea == null ? (yea = new ItemsImplementation()) : yea;
    }
}
