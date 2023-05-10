package io.github.betterclient.client.mod.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    public List<String> values;
    public String value;

    public ModeSetting(String name, String value, String... val) {
        super(name);
        this.value = value;
        this.values = new ArrayList<>(Arrays.asList(val));
    }

    public String getValue() {
        return value;
    }

    public void toggle() {
        if(values.indexOf(value) + 1 >= values.size()) {
            value = values.get(0);
        } else {
            value = values.get(values.indexOf(value) + 1);
        }
    }
}
