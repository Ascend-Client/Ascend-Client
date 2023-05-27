package io.github.betterclient.client.config;

import java.util.List;

public class ClientConfig {
    public interface Config {
        List<Module> mods();
    }

    public interface Module {
        String name();
        boolean toggled();
        List<Setting> settings();
    }

    public interface Setting {
        String name();

        boolean boolVal();
        Color colorVal();
        int keyBindVal();
        String modeVal();
        int numberVal();
    }

    public interface Color {
        int r();
        int g();
        int b();
        int a();
    }
}
