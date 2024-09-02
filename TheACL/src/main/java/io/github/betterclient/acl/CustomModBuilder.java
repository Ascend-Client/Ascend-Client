package io.github.betterclient.acl;

import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.mod.setting.Setting;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomModBuilder {
    @ApiStatus.Internal
    private String modName;
    private Function<Boolean, Boolean> customToggle;
    private final List<Setting> settings = new ArrayList<>();
    private Consumer<Boolean> saveFunction;
    private BooleanSupplier loadFunction;

    @ApiStatus.AvailableSince("1.0")
    public CustomModBuilder setModName(String modName) {
        this.modName = modName;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomModBuilder setCustomToggle(Function<Boolean, Boolean> customToggle) {
        this.customToggle = customToggle;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomModBuilder addOptions(CustomOptionBuilder options) {
        this.settings.addAll(options.generated);
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomModBuilder setSaveFunction(Consumer<Boolean> saveFunction) {
        this.saveFunction = saveFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public CustomModBuilder setLoadFunction(BooleanSupplier loadFunction) {
        this.loadFunction = loadFunction;
        return this;
    }

    @ApiStatus.AvailableSince("1.0")
    public void register() {
        if (this.modName == null)
            throw new IllegalStateException("Mod Name cannot be null");

        Module module = new Module(modName, Category.CUSTOM, null) {
            @Override
            public void toggle() {
                if (customToggle != null) {
                    if (customToggle.apply(!this.toggled)) {
                        super.toggle();
                    }
                    return;
                }

                super.toggle();
            }
        };

        boolean isEnabled = loadFunction.getAsBoolean();
        if (module.isToggled() != isEnabled) {
            module.toggle();
        }

        module.saveFunction = saveFunction;
        settings.forEach(module::addSetting);
        ModuleManager.instance.addModule(module);
    }

    @ApiStatus.AvailableSince("1.0")
    public static CustomModBuilder builder() {
        return new CustomModBuilder();
    }

    @ApiStatus.Internal
    private CustomModBuilder() {}
}
