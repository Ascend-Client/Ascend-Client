package io.github.betterclient.client.mod;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.setting.Setting;

import java.util.List;
import java.util.Vector;

public class Module {
    public String name;
    public boolean toggled;
    public Category cat;
    public BallSack sack = BallSack.getInstance();

    private final List<Setting> settings = new Vector<>();

    public Module(String name, Category cat) {
        this.name = name;
        this.cat = cat;
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
