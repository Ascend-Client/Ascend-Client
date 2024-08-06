package io.github.betterclient.fabric.api;

import java.util.Map;
import java.util.Optional;

public class IconMap {
    Map<Integer, String> map;
    String single = "";

    public IconMap(Map<Integer, String> iconMap) {
        this.map = iconMap;
    }

    public IconMap(String s) {
        this.single = s;
    }


    public Optional<String> get(int size) {
        if(this.single.isEmpty())
            return Optional.ofNullable(map.get(size));
        else
            return Optional.of(single);
    }
}
