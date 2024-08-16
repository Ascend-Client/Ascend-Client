package io.github.betterclient.fabric;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.modremapper.utility.ModLoadingInformation;
import io.github.betterclient.client.util.modremapper.ModRemapper;
import io.github.betterclient.fabric.accesswidener.AccessWidenerApplier;
import io.github.betterclient.fabric.api.CustomValueImpl;
import io.github.betterclient.fabric.api.IconMap;
import io.github.betterclient.fabric.relocate.api.ClientModInitializer;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ContactInformation;
import io.github.betterclient.fabric.relocate.loader.api.metadata.CustomValue;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ModEnvironment;
import io.github.betterclient.fabric.relocate.loader.api.metadata.Person;
import io.github.betterclient.fabric.transformer.PrivateAccessTransformer;
import io.github.betterclient.fabric.transformer.RemoveEntryPointImplements;
import io.github.betterclient.quixotic.Quixotic;
import io.github.betterclient.quixotic.QuixoticClassLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.FabricUtil;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.util.asm.ASM;

import java.io.*;
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
                    List<String> mmainPoints = new ArrayList<>();
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

                    ModEnvironment environment = ModEnvironment.UNIVERSAL;

                    if(obj.has("environment")) {
                        switch (obj.getString("environment").toLowerCase()) {
                            case "client" -> environment = ModEnvironment.CLIENT;
                            case "server" -> environment = ModEnvironment.SERVER;
                            case "*" -> {}
                            default -> throw new RuntimeException(obj.getString("environment"));
                        }
                    }

                    IconMap iconMap = null;

                    if(obj.has("entrypoints")) {
                        JSONObject entrypoints = obj.getJSONObject("entrypoints");

                        for (String key : entrypoints.keySet()) {
                            if(key.equals("client")) {
                                JSONArray arr = entrypoints.getJSONArray(key);
                                for (Object o : arr) {
                                    if(o instanceof String aa) {
                                        mainPoints.add(aa);
                                    } else if(o instanceof HashMap aa) {
                                        mainPoints.add((String) aa.get("value"));
                                    }
                                }
                            }

                            if(key.equals("main")) {
                                JSONArray arr = entrypoints.getJSONArray(key);
                                for (Object o : arr) {
                                    if(o instanceof String aa) {
                                        mmainPoints.add(aa);
                                    } else if(o instanceof HashMap aa) {
                                        mmainPoints.add((String) aa.get("value"));
                                    }
                                }
                            }

                            if(key.equals("preLaunch")) {
                                JSONArray arr = entrypoints.getJSONArray(key);
                                for (Object o : arr) {
                                    if(o instanceof String aa) {
                                        preMainPoints.add(aa);
                                    } else if(o instanceof HashMap aa) {
                                        preMainPoints.add((String) aa.get("value"));
                                    }
                                }
                            }

                            for (Object o : entrypoints.getJSONArray(key).toList()) {
                                if(o instanceof String a) {
                                    allEntries.put(key, a);
                                } else if(o instanceof HashMap aa) {
                                    allEntries.put(key, (String) aa.get("value"));
                                }

                            }
                        }
                    }

                    HashMap<String, CustomValue> values = new HashMap<>();
                    if(obj.has("custom") || obj.has("icon")) {
                        JsonReader reader = new JsonReader(new StringReader(src));

                        reader.beginObject();

                        while (reader.hasNext()) {
                            String key = reader.nextName();

                            if(key.equals("custom")) {
                                if (reader.peek() != JsonToken.BEGIN_OBJECT) {
                                    throw new RuntimeException("Custom values must be in an object!");
                                }

                                reader.beginObject();

                                while (reader.hasNext()) {
                                    values.put(reader.nextName(), CustomValueImpl.readCustomValue(reader));
                                }

                                reader.endObject();
                            } else if(key.equals("icon")) {
                                switch (reader.peek()) {
                                    case STRING:
                                        iconMap = new IconMap(reader.nextString());
                                        break;
                                    case BEGIN_OBJECT:
                                        reader.beginObject();

                                        final SortedMap<Integer, String> imap = new TreeMap<>(Comparator.naturalOrder());

                                        while (reader.hasNext()) {
                                            String kr = reader.nextName();

                                            int size = getSize(kr);

                                            if (reader.peek() != JsonToken.STRING) {
                                                throw new RuntimeException("Icon path must be a string");
                                            }

                                            imap.put(size, reader.nextString());
                                        }

                                        reader.endObject();

                                        if (imap.isEmpty()) {
                                            throw new RuntimeException("Icon object must not be empty!");
                                        }

                                        iconMap = new IconMap(imap);
                                        break;
                                    default:
                                        throw new RuntimeException("Icon entry must be an object or string!");
                                }
                            } else {
                                reader.skipValue();
                            }
                        }

                        reader.endObject();

                        reader.close();
                    }

                    List<Person> authors = new ArrayList<>();
                    if(obj.has("authors")) {
                        JSONArray authorss = obj.getJSONArray("authors");

                        authors.addAll(authorss.toList().stream().map(Object::toString).map(string -> new Person() {
                            @Override
                            public String getName() {
                                return string;
                            }

                            @Override
                            public ContactInformation getContact() {
                                return ContactInformation.EMPTY;
                            }
                        }).toList());
                    }

                    List<Person> contributors = new ArrayList<>();
                    if(obj.has("contributors")) {
                        JSONArray authorss = obj.getJSONArray("contributors");

                        authors.addAll(authorss.toList().stream().map(Object::toString).map(string -> new Person() {
                            @Override
                            public String getName() {
                                return string;
                            }

                            @Override
                            public ContactInformation getContact() {
                                return ContactInformation.EMPTY;
                            }
                        }).toList());
                    }

                    ContactInformation information;
                    if(obj.has("contact")) {
                        JSONObject co = obj.getJSONObject("contact");
                        Map<String, String> yes = new HashMap<>();

                        for (String s : co.keySet()) {
                            yes.put(s, co.getString(s));
                        }

                        information = new ContactInformation() {
                            @Override
                            public Optional<String> get(String key) {
                                return Optional.ofNullable(yes.get(key));
                            }

                            @Override
                            public Map<String, String> asMap() {
                                return yes;
                            }
                        };
                    } else
                        information = ContactInformation.EMPTY;

                    String description = "";
                    if(obj.has("description"))
                        description = obj.getString("description");

                    ModEnvironment finalEnvironment = environment;
                    String finalDescription = description;
                    IconMap finalIconMap = iconMap;
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
                        public List<String> mainEntries() {
                            return mmainPoints;
                        }

                        @Override
                        public Map<String, String> allEntries() {
                            return allEntries;
                        }

                        @Override
                        public Map<String, CustomValue> customValues() {
                            return values;
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

                        @Override
                        public ModEnvironment environment() {
                            return finalEnvironment;
                        }

                        @Override
                        public String description() {
                            return finalDescription;
                        }

                        @Override
                        public ContactInformation getContact() {
                            return information;
                        }

                        @Override
                        public Collection<Person> contributors() {
                            return contributors;
                        }

                        @Override
                        public Collection<Person> authors() {
                            return authors;
                        }

                        @Override
                        public IconMap getIconMap() {
                            return finalIconMap;
                        }
                    };
                }
            }

            if(loaded != null) {
                loadedMods.add(loaded);

                Quixotic.classLoader.addURL(f.toURI().toURL());

                if(!loaded.accessWidener().isEmpty())
                    Quixotic.classLoader.addPlainTransformer(new AccessWidenerApplier(loaded.accessWidener()));

                IBridge.getPreLaunch().info("Successfully loaded mod: " + loaded.name() + " (" + loaded.from().getAbsolutePath() + ")");
                return loaded;
            }
        } catch (Exception e) {
            FabricErrorReporter.exception(f.getAbsolutePath(), e).print();
        }
        return null;
    }

    private static int getSize(String kr) {
        int size;

        try {
            size = Integer.parseInt(kr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse icon size '" + kr + "'!", e);
        }

        if (size < 1) {
            throw new RuntimeException("Size must be positive!");
        }
        return size;
    }

    private void loadMod(String containerMod, File mod) {
        if(this.loadMod(mod) != null)
            loadedMods.getLast().setContainer(containerMod);
    }

    public static FabricLoader getInstance() {
        return instance;
    }

    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addPlainTransformer(new RemoveEntryPointImplements());
        quixoticClassLoader.addPlainTransformer(new PrivateAccessTransformer());
    }

    public void doMixin() {
        if(ASM.getApiVersionMinor() < 5) {
            try {
                Field f = ASM.class.getDeclaredField("minorVersion");
                f.setAccessible(true);
                f.setInt(null, 5);

                f = ASM.class.getDeclaredField("implMinorVersion");
                f.setAccessible(true);
                f.setInt(null, 5);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

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

        HashMap<String, FabricMod> configToModMap = new HashMap<>();
        loadedMods.forEach(fabricMod -> fabricMod.mixinConfigs().forEach(string -> {
            Mixins.addConfiguration(string);
            configToModMap.put(string, fabricMod);
        }));

        try {
            IMixinConfig.class.getMethod("decorate", String.class, Object.class);
            apply(configToModMap);
        } catch (NoSuchMethodException e) {
            System.err.println("Mixin version doesn't support decoration");
            e.printStackTrace(System.err);
        }
    }

    private void apply(HashMap<String, FabricMod> configToModMap) {
        for (Config rawConfig : Mixins.getConfigs()) {
            FabricMod mod = configToModMap.get(rawConfig.getName());
            if (mod == null) {
                IMixinConfig config = rawConfig.getConfig();
                config.decorate(FabricUtil.KEY_MOD_ID, "Ballsack_Client");

                continue;
            }

            IMixinConfig config = rawConfig.getConfig();
            config.decorate(FabricUtil.KEY_MOD_ID, mod.id());
        }
    }

    public void callClientMain() throws Exception {
        this.callMain();

        for (FabricMod mod : loadedMods) {
            for (String entry : mod.clientEntries()) {
                System.out.println(entry + " call");
                if(entry.contains("::")) {
                    Class<?> loadedMod = Class.forName(entry.substring(0, entry.indexOf(":")), false, Quixotic.classLoader);
                    String methodName = entry.substring(entry.lastIndexOf(":") + 1);
                    try {
                        Field f = loadedMod.getField(methodName);
                        ClientModInitializer theRunnable;
                        if(Modifier.isStatic(f.getModifiers())) {
                            theRunnable = (ClientModInitializer) f.get(null);
                        } else {
                            theRunnable = (ClientModInitializer) f.get(loadedMod.getConstructor().newInstance());
                        }
                        theRunnable.onInitializeClient();
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
                    if(method.getName().equals("onInitializeClient"))
                        foundInit = method;
                }

                if(foundInit == null)
                    continue;
                foundInit.invoke(loadedMod.getConstructor().newInstance());
            }
        }
    }

    public void callMain() throws Exception {
        for (FabricMod mod : loadedMods) {
            for (String entry : mod.mainEntries()) {
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
                    if(method.getName().equals("onInitialize"))
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