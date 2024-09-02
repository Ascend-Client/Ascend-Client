package io.github.betterclient.client.mod;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.setting.Setting;
import io.github.betterclient.client.util.FileResource;

import java.util.List;
import java.util.Vector;

public class Module {
    public String name;
    public boolean toggled;
    public Category cat;
    public Ascend sack = Ascend.getInstance();
    public final IBridge.Identifier icon;

    private final List<Setting> settings = new Vector<>();

    public Module(String name, Category cat, IBridge.Identifier icon) {
        this.name = name;
        this.cat = cat;
        this.icon = icon;
        if(icon != null) {
            String s = icon.path;
            s = s.substring(s.indexOf("/") + 1);
            Ascend.getInstance().resources.put(icon, new FileResource("/assets/" + s));
        }

    }

    public boolean isToggled() {
        return toggled;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return cat;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void addSetting(Setting s) {
        this.settings.add(s);
    }

    public Setting getSetting(String name) {
        return settings.stream().filter(setting -> setting.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void toggle() {
        toggled = !toggled;
        if(toggled) {
            onEnabled();
            sack.bus.subscribe(this);
        }
        else {
            onDisabled();
            sack.bus.unSubscribe(this);
        }
    }

    public void onEnabled() {}
    public void onDisabled() {}
}
