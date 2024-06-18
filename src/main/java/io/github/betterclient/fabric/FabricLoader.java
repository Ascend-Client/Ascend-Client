package io.github.betterclient.fabric;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.modremapper.ModLoadingInformation;
import io.github.betterclient.client.util.modremapper.ModRemapper;
import io.github.betterclient.fabric.accesswidener.AccessWidenerApplier;
import io.github.betterclient.fabric.transformer.PrivateAccessTransformer;
import io.github.betterclient.fabric.transformer.RemoveEntryPointImplements;
import io.github.betterclient.fabric.transformer.RemoveInitializer;
import io.github.betterclient.quixotic.Quixotic;
import io.github.betterclient.quixotic.QuixoticClassLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FabricLoader {
    private static final FabricLoader instance = new FabricLoader();
    public List<FabricMod> loadedMods = new ArrayList<>();

    public boolean isMod(JarFile f) {
        return f.getEntry("fabric.mod.json") != null;
    }

    public FabricMod loadMod(File f) {
        if(f == null) {
            FabricErrorReporter.exception("", new RuntimeException("f == null")).print();
            return null;
        }

        if(f.getName().contains("fabric-crash-report-info")) {
            IBridge.getPreLaunch().info("Skipping mod \"Fabric Crash Report Info\"");
            return null;
        }

        if(f.getName().contains("fabric-biome-api")) {
            IBridge.getPreLaunch().info("Skipping mod \"Fabric Biome Api\"");
            return null;
        }

        try {
            JarFile mod = new JarFile(f);
            if(!isMod(mod)) {
                IBridge.getPreLaunch().info("Mod file: " + f.getName() + " isn't a mod.");
                return null;
            }

            FabricMod loaded = null;

            for (JarEntry entry : Util.getEntries(mod)) {
                if(entry.getName().equals("fabric.mod.json")) {
                    String src = new String(Util.readAndClose(mod.getInputStream(entry)));
                    JSONObject obj = new JSONObject(src);
                    String name;
                    String id = obj.getString("id");
                    if(obj.has("name"))
                        name = obj.getString("name");
                    else
                        name = id;


                    for (FabricMod loadedMod : this.loadedMods) {
                        if(loadedMod.name().equals(name)) {
                            IBridge.getPreLaunch().info("Mod " + loadedMod.name() + " already loaded!");
                            return loadedMod;
                        }
                    }

                    if(obj.has("jars")) {
                        for (Object jars : obj.getJSONArray("jars")) {
                            JSONObject objj = (JSONObject) jars;
                            String path = objj.getString("file");

                            InputStream is = mod.getInputStream(mod.getEntry(path));
                            File dir = new File(Application.modLoadingInformation.state().equals(ModLoadingInformation.State.LOADING_BUILTIN) ? Application.modJarsFolder : Application.customJarsFolder, "builtin");
                            File ff = new File(dir, path.substring(path.lastIndexOf('/') + 1));
                            Files.createDirectories(dir.toPath());
                            ff.delete();
                            ff.createNewFile();
                            Files.write(ff.toPath(), is.readAllBytes());
                            is.close();

                            if(isMod(new JarFile(ff))) {
                                try {
                                    this.loadMod(name, Application.modLoadingInformation.state().equals(ModLoadingInformation.State.LOADING_BUILTIN) ? ModRemapper.remapInternalMod(ff, true) : ModRemapper.remapMod(ff, true));
                                } catch (Exception e) {
                                    FabricErrorReporter.exception("Nested Mod in: " + name, e).print();
                                    //Peacefully continue with other mods
                                }
                            } else {
                                Quixotic.classLoader.addURL(ff.toURI().toURL());
                            }
                        }
                    }

                    JSONArray mixes = obj.has("mixins") ? obj.getJSONArray("mixins") : new JSONArray();
                    String accessWidener = obj.has("accessWidener") ? obj.getString("accessWidener") : "";
                    String version = obj.getString("version");

                    List<String> mainPoints = new ArrayList<>();
                    List<String> preMainPoints = new ArrayList<>();
                    Map<String, String> allEntries = new HashMap<>();
                    List<String> mixins = new ArrayList<>();
                    for (int i = 0; i < mixes.length(); i++) {
                        if(mixes.get(i) instanceof String) {
                            mixins.add(mixes.getString(i));
                        } else {
                            if(mixes.getJSONObject(i).getString("environment").equals("client"))
                                mixins.add(mixes.getJSONObject(i).getString("config"));
                        }
                    }

                    if(obj.has("entrypoints")) {
                        JSONObject entrypoints = obj.getJSONObject("entrypoints");

                        for (String key : entrypoints.keySet()) {
                            if(key.equals("main") || key.equals("client")) {
                                mainPoints.addAll(entrypoints.getJSONArray(key).toList().stream().map(String.class::cast).toList());
                            }

                            if(key.equals("preLaunch")) {
                                preMainPoints.addAll(entrypoints.getJSONArray(key).toList().stream().map(String.class::cast).toList());
                            }

                            for (Object o : entrypoints.getJSONArray(key).toList()) {
                                allEntries.put(key, (String) o);
                            }
                        }
                    }

                    loaded = new FabricMod() {
                        @Override
                        public String name() {
                            return name;
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
                        public Map<String, String> allEntries() {
                            return allEntries;
                        }

                        @Override
                        public List<String> mixinConfigs() {
                            return mixins;
                        }

                        @Override
                        public String accessWidener() {
                            return accessWidener;
                        }

                        @Override
                        public File from() {
                            return f;
                        }
                        String a = null;
                        @Override
                        public String getContainer() {
                            return a;
                        }

                        @Override
                        public void setContainer(String s) {
                            a = s;
                        }

                        @Override
                        public String version() {
                            return version;
                        }

                        @Override
                        public String id() {
                            return id;
                        }
                    };
                }
            }

            if(loaded != null) {
                loadedMods.add(loaded);

                Quixotic.classLoader.addURL(f.toURI().toURL());

                if(!loaded.accessWidener().equals(""))
                    Quixotic.classLoader.addPlainTransformer(new AccessWidenerApplier(loaded.accessWidener()));

                IBridge.getPreLaunch().info("Successfully loaded mod: " + loaded.name() + " (" + loaded.from().getAbsolutePath() + ")");
                return loaded;
            }
        } catch (Exception e) {
            FabricErrorReporter.exception(f.getAbsolutePath(), e).print();
        }
        return null;
    }

    private void loadMod(String containerMod, File mod) {
        if(this.loadMod(mod) != null)
            loadedMods.get(loadedMods.size() - 1).setContainer(containerMod);
    }

    public static FabricLoader getInstance() {
        return instance;
    }

    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addPlainTransformer(new RemoveEntryPointImplements());
        quixoticClassLoader.addPlainTransformer(new PrivateAccessTransformer());
        quixoticClassLoader.addPlainTransformer(new RemoveInitializer());
    }

    public List<String> getMixinConfigurations() {
        try {
            for (FabricMod mod : loadedMods) {
                for (String preLaunch : mod.preMainEntries()) {
                    if(preLaunch.equals("com.replaymod.core.MixinExtrasInit"))
                        continue;

                    Class<?> loadedMod = Class.forName(preLaunch, false, Quixotic.classLoader);
                    loadedMod.getDeclaredMethod("onPreLaunch").invoke(loadedMod.getConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            IBridge.getPreLaunch().error(e.toString());
        }

        ArrayList<String> total = new ArrayList<>();

        for (FabricMod mod : loadedMods) {
            total.addAll(mod.mixinConfigs());
        }

        return total;
    }

    public void callClientMain() throws Exception {
        for (FabricMod mod : loadedMods) {
            for (String entry : mod.clientEntries()) {
                if(entry.contains("::")) {
                    Class<?> loadedMod = Class.forName(entry.substring(0, entry.indexOf(":")), false, Quixotic.classLoader);
                    String methodName = entry.substring(entry.lastIndexOf(":") + 1);
                    try {
                        Field f = loadedMod.getField(methodName);
                        Runnable theRunnable;
                        if(Modifier.isStatic(f.getModifiers())) {
                            theRunnable = (Runnable) f.get(null);
                        } else {
                            theRunnable = (Runnable) f.get(loadedMod.getConstructor().newInstance());
                        }
                        theRunnable.run();
                    } catch (NoSuchFieldException exception) {
                        Method mde = loadedMod.getDeclaredMethod(methodName);
                        if(Modifier.isStatic(mde.getModifiers())) {
                            mde.invoke(null);
                        } else {
                            mde.invoke(loadedMod.getConstructor().newInstance());
                        }
                    }

                    continue;
                }

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

    public String getModName(File f) throws IOException {
        JarFile mod = new JarFile(f);
        if(!isMod(mod)) {
            IBridge.getPreLaunch().info("Mod file: " + f.getName() + " isn't a mod.");
            return null;
        }
        JarEntry modJson = mod.getJarEntry("fabric.mod.json");
        String src = new String(Util.readAndClose(mod.getInputStream(modJson)));
        JSONObject obj = new JSONObject(src);

        String name;
        String id = obj.getString("id");
        if(obj.has("name"))
            name = obj.getString("name");
        else
            name = id;
        return name;
    }
}