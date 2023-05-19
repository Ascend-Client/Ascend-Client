package io.github.betterclient.fabric;

import io.github.betterclient.fabric.transformer.PrivateAccessTransformer;
import io.github.betterclient.fabric.transformer.RedirectFabricCalls;
import io.github.betterclient.fabric.transformer.RemoveEntryPointImplements;
import io.github.betterclient.quixotic.Quixotic;
import io.github.betterclient.quixotic.QuixoticApplication;
import io.github.betterclient.quixotic.QuixoticClassLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FabricLoader implements QuixoticApplication {
    private static final FabricLoader instance = new FabricLoader();
    public List<FabricMod> loadedMods = new ArrayList<>();

    public void loadMod(File f) throws Exception {
        JarFile mod = new JarFile(f);
        List<JarEntry> entries = Util.getEntries(mod);
        FabricMod loaded = null;

        for (JarEntry entry : entries) {
            if(entry.getName().equals("fabric.mod.json")) {
                String src = new String(Util.readAndClose(mod.getInputStream(entry)));
                JSONObject obj = new JSONObject(src);
                JSONArray mixes = obj.getJSONArray("mixins");

                String name = obj.getString("name");
                String accessWidener = obj.getString("accessWidener");
                List<String> mainPoints = new ArrayList<>();
                List<String> preMainPoints = new ArrayList<>();
                List<String> mixins = new ArrayList<>(mixes.toList().stream().map(String.class::cast).toList());

                JSONObject entrypoints = obj.getJSONObject("entrypoints");

                for (String key : entrypoints.keySet()) {
                    if(key.equals("main") || key.equals("client")) {
                        mainPoints.addAll(entrypoints.getJSONArray(key).toList().stream().map(String.class::cast).toList());
                    }

                    if(key.equals("preLaunch")) {
                        preMainPoints.addAll(entrypoints.getJSONArray(key).toList().stream().map(String.class::cast).toList());
                    }
                }

                loaded = new FabricMod() {
                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public String accessWidener() {
                        return accessWidener;
                    }

                    @Override
                    public List<String> clientEntries() {
                        return mainPoints;
                    }

                    @Override
                    public List<String> preMainEntries() {
                        return preMainPoints;
                    }

                    @Override
                    public List<String> mixinConfigs() {
                        return mixins;
                    }
                };
            }
        }

        if(loaded == null) {
            throw new RuntimeException("Couldn't load mod, possible error. Report this.");
        } else {
            loadedMods.add(loaded);

            Method m = QuixoticClassLoader.class.getDeclaredMethod("addURL", URL.class);
            m.setAccessible(true);
            m.invoke(Quixotic.classLoader, f.toURI().toURL());
        }

    }

    public static FabricLoader getInstance() {
        return instance;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getApplicationVersion() {
        return null;
    }

    @Override
    public String getMainClass() {
        return null;
    }

    @Override
    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addPlainTransformer(new RemoveEntryPointImplements());
        quixoticClassLoader.addPlainTransformer(new RedirectFabricCalls());
        quixoticClassLoader.addPlainTransformer(new PrivateAccessTransformer());

        try {
            for (FabricMod mod : loadedMods) {
                for (String preLaunch : mod.preMainEntries()) {
                    Class<?> loadedMod = Class.forName(preLaunch, false, quixoticClassLoader);

                    loadedMod.getDeclaredMethod("onPreLaunch").invoke(loadedMod.getConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getMixinConfigurations() {
        ArrayList<String> total = new ArrayList<>();

        for (FabricMod mod : loadedMods) {
            total.addAll(mod.mixinConfigs());
        }

        return total;
    }

    public void callClientMain() throws Exception {
        for (FabricMod mod : loadedMods) {
            for (String entry : mod.clientEntries()) {
                Class<?> loadedMod = Class.forName(entry, false, Quixotic.classLoader);

                Method foundInit = null;

                for (Method method : loadedMod.getDeclaredMethods()) {
                    if(method.getName().equals("onInitializeClient") || method.getName().equals("onInitialize"))
                        foundInit = method;
                }

                if(foundInit == null)
                    continue;
                foundInit.invoke(loadedMod.getConstructor().newInstance());
            }
        }
    }
}