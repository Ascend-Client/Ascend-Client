package io.github.betterclient.client.config;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.config.impl.ClientImplementation;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class Config {
    public File currentConfig = new File(Application.configFolder, "current.txt");
    public File loadedConfig;

    public void load() {
        try {
            String config = "main";

            if(!currentConfig.exists()) {
                createMainConfig();
            } else {
                config = Files.readString(currentConfig.toPath());
            }

            File toLoad = new File(Application.configFolder, config + ".json");
            if(!toLoad.exists()) {
                createMainConfig();
                toLoad = new File(Application.configFolder, "main.json");
            }

            loadedConfig = toLoad;

            ClientConfig.Config loaded = this.convertJsonToConfig(new JSONObject(Files.readString(toLoad.toPath())));

            for (ClientConfig.Module mod : loaded.mods()) {
                if(!Ascend.getInstance().moduleManager.hasModule(mod.name())) continue;

                Module clientMod = Ascend.getInstance().moduleManager.getModuleByName(mod.name());

                if(mod.toggled() != clientMod.toggled) {
                    clientMod.toggle();
                }

                for (ClientConfig.Setting setting : mod.settings()) {
                    Setting s = clientMod.getSetting(setting.name());

                    if(s instanceof NoneSetting) continue;

                    if(s == null) {
                        if(clientMod instanceof HUDModule hud) {
                            if(setting.name().equals("X")) {
                                hud.renderable.x = setting.numberVal();
                            } else if(setting.name().equals("Y")) {
                                hud.renderable.y = setting.numberVal();
                            } else continue;
                        } else continue;
                    }

                    if(s instanceof BooleanSetting set && set.value != setting.boolVal()) {
                        set.toggle();
                    }

                    if(s instanceof ColorSetting set) {
                        ClientConfig.Color color = setting.colorVal();

                        set.setColor(new Color(color.r(), color.g(), color.b(), color.a()));
                    }

                    if(s instanceof KeyBindSetting set) {
                        set.key = setting.keyBindVal();
                        set.bind.setKey(setting.keyBindVal());
                    }

                    if(s instanceof ModeSetting set) {
                        while (!set.value.equals(setting.modeVal())) {
                            set.toggle();
                        }
                    }

                    if(s instanceof NumberSetting set) {
                        set.value = setting.numberVal();
                    }
                }
            }
        } catch (Exception e) {
            IBridge.getPreLaunch().error(e.toString());
        }
    }

    public void save() {
        try {
            String json = convertConfigToJson(generateConfig()).toString(4);

            FileWriter writer = new FileWriter(loadedConfig);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            IBridge.getPreLaunch().error(e.toString());
        }
    }

    public void switchConfig(String to) {
        try {
            File config = new File(Application.configFolder, to + ".json");
            if(!config.exists()) {
                config.createNewFile();

                FileWriter writer = new FileWriter(config);
                writer.write(convertConfigToJson(generateConfig()).toString(4));
                writer.close();
            }

            loadedConfig = config;
            currentConfig.delete();
            currentConfig.createNewFile();
            FileWriter writer1 = new FileWriter(currentConfig);
            writer1.write(to);
            writer1.close();

            load();
        } catch (Exception e) {
            IBridge.getPreLaunch().error(e.toString());
        }
    }

    private void createMainConfig() {
        try {
            currentConfig.createNewFile();
            Writer w = new FileWriter(currentConfig);
            w.write("main");
            w.close();
            (loadedConfig = new File(Application.configFolder, "main.json")).createNewFile();
            save();
        } catch (IOException e) {
            IBridge.getPreLaunch().error(e.toString());
        }
    }

    private ClientConfig.Config generateConfig() {
        return new ClientImplementation();
    }

    private JSONObject convertConfigToJson(ClientConfig.Config config) {
        JSONObject configJson = new JSONObject();
        JSONArray modsJsonArray = new JSONArray();

        for (ClientConfig.Module module : config.mods()) {
            JSONObject moduleJson = new JSONObject();
            moduleJson.put("name", module.name());
            moduleJson.put("toggled", module.toggled());
            JSONArray settingsJsonArray = new JSONArray();

            for (ClientConfig.Setting setting : module.settings()) {
                JSONObject settingJson = new JSONObject();
                settingJson.put("name", setting.name());
                settingJson.put("boolVal", setting.boolVal());
                settingJson.put("colorVal", convertColorToJson(setting.colorVal()));
                settingJson.put("keyBindVal", setting.keyBindVal());
                settingJson.put("modeVal", setting.modeVal());
                settingJson.put("numberVal", setting.numberVal());
                settingsJsonArray.put(settingJson);
            }

            moduleJson.put("settings", settingsJsonArray);
            modsJsonArray.put(moduleJson);
        }

        configJson.put("mods", modsJsonArray);
        return configJson;
    }

    private JSONObject convertColorToJson(ClientConfig.Color color) {
        JSONObject colorJson = new JSONObject();
        colorJson.put("r", color.r());
        colorJson.put("g", color.g());
        colorJson.put("b", color.b());
        colorJson.put("a", color.a());
        return colorJson;
    }

    private ClientConfig.Config convertJsonToConfig(JSONObject configJson) {
        java.util.List<ClientConfig.Module> modules = new ArrayList<>();

        JSONArray modsJsonArray = configJson.getJSONArray("mods");
        for (int i = 0; i < modsJsonArray.length(); i++) {
            JSONObject moduleJson = modsJsonArray.getJSONObject(i);
            String moduleName = moduleJson.getString("name");
            boolean moduleToggled = moduleJson.getBoolean("toggled");

            JSONArray settingsJsonArray = moduleJson.getJSONArray("settings");
            java.util.List<ClientConfig.Setting> settings = new ArrayList<>();

            for (int j = 0; j < settingsJsonArray.length(); j++) {
                JSONObject settingJson = settingsJsonArray.getJSONObject(j);
                String settingName = settingJson.getString("name");
                boolean boolVal = settingJson.getBoolean("boolVal");
                ClientConfig.Color colorVal = convertJsonToColor(settingJson.getJSONObject("colorVal"));
                int keyBindVal = settingJson.getInt("keyBindVal");
                String modeVal = settingJson.getString("modeVal");
                int numberVal = settingJson.getInt("numberVal");

                ClientConfig.Setting setting = new ClientConfig.Setting() {
                    @Override
                    public String name() {
                        return settingName;
                    }

                    @Override
                    public boolean boolVal() {
                        return boolVal;
                    }

                    @Override
                    public ClientConfig.Color colorVal() {
                        return colorVal;
                    }

                    @Override
                    public int keyBindVal() {
                        return keyBindVal;
                    }

                    @Override
                    public String modeVal() {
                        return modeVal;
                    }

                    @Override
                    public int numberVal() {
                        return numberVal;
                    }
                };

                settings.add(setting);
            }

            ClientConfig.Module module = new ClientConfig.Module() {
                @Override
                public String name() {
                    return moduleName;
                }

                @Override
                public boolean toggled() {
                    return moduleToggled;
                }

                @Override
                public java.util.List<ClientConfig.Setting> settings() {
                    return settings;
                }
            };

            modules.add(module);
        }

        return () -> modules;
    }

    private ClientConfig.Color convertJsonToColor(JSONObject colorJson) {
        int r = colorJson.getInt("r");
        int g = colorJson.getInt("g");
        int b = colorJson.getInt("b");
        int a = colorJson.getInt("a");

        return new ClientConfig.Color() {
            @Override
            public int r() {
                return r;
            }

            @Override
            public int g() {
                return g;
            }

            @Override
            public int b() {
                return b;
            }

            @Override
            public int a() {
                return a;
            }
        };
    }

}
