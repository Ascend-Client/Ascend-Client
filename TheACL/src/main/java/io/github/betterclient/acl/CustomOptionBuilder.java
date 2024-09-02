package io.github.betterclient.acl;

import io.github.betterclient.client.mod.setting.*;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomOptionBuilder {
    @ApiStatus.Internal
    List<Setting> generated = new ArrayList<>();

    @ApiStatus.Internal
    private CustomOptionBuilder() {}

    @ApiStatus.AvailableSince("1.0")
    public static CustomOptionBuilder builder() {
        return new CustomOptionBuilder();
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addIntegerOption(String name, Supplier<Integer> value, int min, int max, Consumer<Integer> saveFunction) {
        this.generated.add(new NumberSetting(name, value.get(), min, max));
        this.generated.getLast().saveFunc = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addDropdownOption(String name, Supplier<String> value, Consumer<String> saveFunction, String... values) {
        this.generated.add(new ModeSetting(name, value.get(), values));
        this.generated.getLast().saveFunc = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addColorOption(String name, Supplier<Color> value, Consumer<Integer> saveFunction) {
        this.generated.add(new ColorSetting(name, value.get()));
        this.generated.getLast().saveFunc = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addBooleanOption(String name, Supplier<Boolean> value, Consumer<Boolean> saveFunction) {
        this.generated.add(new BooleanSetting(name, value.get()));
        this.generated.getLast().saveFunc = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addKeyBindOption(String name, Supplier<Integer> value, Consumer<Integer> saveFunction, Runnable press, Runnable unPress) {
        this.generated.add(new KeyBindSetting(name, value.get(), press, unPress));
        this.generated.getLast().saveFunc = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomOptionBuilder addSpace(String name) {
        this.generated.add(new NoneSetting(name));
        this.generated.getLast().saveFunc = o -> {};
        return this;
    }
}