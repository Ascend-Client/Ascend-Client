package io.github.betterclient.client.util.modremapper;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.DownloadedMinecraft;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import io.github.betterclient.client.util.modremapper.mixin.method.RecursiveAnnotationMapper;
import io.github.betterclient.client.util.modremapper.utility.ModIssueFixer;
import io.github.betterclient.client.util.modremapper.utility.ModLoadingInformation;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.json.JSONObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility.*;

public class ModRemapper {
    //false for refmap mapping
    //true for mixin mapping
    private static boolean mappingMethod = false;

    public static File remapMod(File toRemap, boolean isBuiltin) throws IOException {
        DownloadedMinecraft version = Application.minecraft;
        return remapMod(toRemap, isBuiltin, false, version.intermediaryToYarn(), version.intermediaryJar());
    }

    public static File remapInternalMod(File toRemap, boolean isBuiltin) throws IOException {
        DownloadedMinecraft version = Application.minecraft;
        return remapMod(toRemap, isBuiltin, true, version.intermediaryToYarn(), version.intermediaryJar());
    }

    private static File remapMod(File modToRemap, boolean isBuiltin, boolean isInternal, File mappingsFile, File mappingJar) throws IOException {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();

        ModLoadingInformation old = Application.modLoadingInformation;
        Application.modLoadingInformation = new ModLoadingInformation(old.minecraftClasses(), old.nonCustomMods(), old.state(), modToRemap);

        File remappedMod;

        if(isInternal) {
            remappedMod = new File(Application.remappedModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
            if(isBuiltin) {
                remappedMod = new File(Application.remappedBuiltinModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
            }
        } else {
            remappedMod = new File(Application.remappedModsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
            if(isBuiltin) {
                remappedMod = new File(Application.remappedModsFolder, "builtin");
                Files.createDirectories(remappedMod.toPath());
                remappedMod = new File(remappedMod, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
            }
        }

        if(remappedMod.exists() && !Application.doRemappingOfAlreadyRemappedMods) {
            if(Util.readAndClose(new FileInputStream(remappedMod)).length != 0)
                return remappedMod;
            else
                bridge.info("Found corrupted mod file, deleting and remapping");
        }
        String modName = FabricLoader.getInstance().getModName(modToRemap);

        bridge.info("Remapping mod " + modName);

        remappedMod.delete();
        if(!remappedMod.createNewFile()) {
            bridge.error("Failed to create file (?)");
        }

        Map<String, byte[]> finalFile = map(modToRemap, mappingsFile, mappingJar);

        mappingMethod = true;

        JarFile file = new JarFile(modToRemap);
        for (JarEntry entry : Util.getEntries(file)) {
            if(!entry.getName().endsWith(".class") && !entry.getName().equals("META-INF/MANIFEST.MF")) {
                byte[] bites = Util.readAndClose(file.getInputStream(entry));
                if(entry.getName().endsWith(".json")) {
                    String str = new String(bites);
                    str = String.join("\n", Arrays.stream(str.split("\n")).filter(string -> !string.contains("//")).toArray(String[]::new));

                    determineMappingMethod(str);
                    str = str.replace("\"MixinMinecraft_NoAuthInDev\",", "");
                    bites = str.getBytes();
                }

                if(entry.getName().equals("fabric-screen-api-v1.mixins.json") && modName.equals("Fabric Screen API (v1)") && (Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C || Application.minecraft.version().version() == MinecraftVersion.Version.V1_19_4)) {
                    bites = new String(bites).replace("\"GameRendererMixin\",", "").getBytes();
                }

                finalFile.put(entry.getName(), bites);
            }
        }

        Map<String, String> tinyMappings = new HashMap<>();
        populateMappings(tinyMappings, mappingsFile);

        for (String s : new ArrayList<>(finalFile.keySet())) {
            byte[] bytes = finalFile.get(s);

            if(s.endsWith(".json")) {
                bytes = modifyRefmap(new String(bytes), tinyMappings).getBytes();
            } else if(s.endsWith(".lang")) {
                s = s.substring(0, s.lastIndexOf('.')) + ".json";
                bytes = convertLangToJSON(bytes);
            } else if(s.endsWith(".accesswidener")) {
                bytes = modifyAccessWidener(bytes, tinyMappings);
            } else if(s.endsWith(".class")) {
                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                try {
                    ModIssueFixer.edit(node, modToRemap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if(detectMixin(node)) {
                    //Shadow fields/methods should be mapped in either mapping method
                    //Overwrite methods too
                    for (MethodNode method : node.methods) {
                        if(method.visibleAnnotations == null) {
                            if(Modifier.isStatic(method.access) && !Modifier.isAbstract(node.access)) {
                                method.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")));
                                method.access = Opcodes.ACC_STATIC + Opcodes.ACC_PRIVATE;
                            }

                            continue;
                        }
                        for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                            if(mappingMethod) {
                                MixinMethodMapper.mapAll(node, method, visibleAnnotation, tinyMappings);
                            } else {
                                if(MixinMethodMapper.SHADOW_MAPPER.detect(visibleAnnotation)) {
                                    MixinMethodMapper.SHADOW_MAPPER.mapMixin(node, method, visibleAnnotation, tinyMappings);
                                    break;
                                }

                                if(MixinMethodMapper.OVERWRITE_MAPPER.detect(visibleAnnotation)) {
                                    MixinMethodMapper.OVERWRITE_MAPPER.mapMixin(node, method, visibleAnnotation, tinyMappings);
                                    break;
                                }
                            }
                        }
                    }
                    for (FieldNode field : node.fields) {
                        if(field.visibleAnnotations == null) continue;
                        for (AnnotationNode visibleAnnotation : field.visibleAnnotations) {
                            if(MixinMethodMapper.SHADOW_MAPPER.detect(visibleAnnotation)) {
                                MixinMethodMapper.SHADOW_MAPPER.mapMixin(field, tinyMappings);
                                break;
                            }
                        }
                    }
                }

                for (MethodNode method : node.methods) {
                    if(method.name.startsWith("method_")) {
                        method.name = tinyMappings.getOrDefault(method.name, method.name);
                    }

                    for (AbstractInsnNode instruction : method.instructions) {
                        if(instruction instanceof MethodInsnNode minnode && minnode.name.startsWith("method_")) {
                            minnode.name = tinyMappings.getOrDefault(minnode.name, minnode.name);
                        }

                        if(instruction instanceof FieldInsnNode finnode && finnode.name.startsWith("field_")) {
                            finnode.name = tinyMappings.getOrDefault(finnode.name, finnode.name);
                        }

                        if(instruction instanceof InvokeDynamicInsnNode idin) {
                            if(idin.name.startsWith("method_")) {
                                idin.name = tinyMappings.getOrDefault(idin.name, idin.name);
                            }

                            if(idin.bsmArgs.length >= 3 && idin.bsmArgs[1] instanceof Handle h) {
                                if(h.getName().startsWith("method_")) {
                                    idin.bsmArgs[1] = new Handle(h.getTag(), h.getOwner(), tinyMappings.getOrDefault(h.getName(), h.getName()), h.getDesc(), h.getTag() == Opcodes.H_INVOKEINTERFACE);
                                }
                            }
                        }
                    }
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                bytes = writer.toByteArray();
            } else {continue;}
            finalFile.put(s, bytes);
        }

        if(mappingMethod) {
            for (String s : new ArrayList<>(finalFile.keySet())) {
                byte[] bytes = finalFile.get(s);

                if(s.endsWith(".class")) {
                    ClassReader reader = new ClassReader(bytes);
                    ClassNode node = new ClassNode();
                    reader.accept(node, 0);

                    for (MethodNode method : node.methods) {
                        for (AbstractInsnNode instruction : method.instructions) {
                            if(instruction instanceof MethodInsnNode minnode) {
                                if(MixinMethodMapper.allMappedNames.containsKey(minnode.name)) {
                                    String oo = MixinMethodMapper.allMappedNames.get(minnode.name);
                                    String ownerClass = oo.split("-")[0];
                                    String methodName = oo.split("-")[1];

                                    if(ownerClass.equals(minnode.owner)) {
                                        minnode.name = methodName;
                                    }
                                }
                            }
                        }
                    }

                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    node.accept(writer);
                    bytes = writer.toByteArray();
                }

                finalFile.put(s, bytes);
            }
        }

        JarOutputStream jos = new JarOutputStream(Files.newOutputStream(remappedMod.toPath()));

        for (String s : finalFile.keySet()) {
            byte[] bytes = finalFile.get(s);

            jos.putNextEntry(new JarEntry(s));
            jos.write(bytes);
            jos.closeEntry();
        }

        jos.close();

        return remappedMod;
    }

    private static String modifyRefmap(String old, Map<String, String> maps) {
        JSONObject obj = new JSONObject(old);

        if (!obj.has("mappings") || !obj.has("data")) {
            return obj.toString(6);
        }

        JSONObject mappings = obj.getJSONObject("mappings");

        for (String map : mappings.keySet()) {
            JSONObject objAtPoint = mappings.getJSONObject(map);

            for (String toMap : objAtPoint.keySet()) {
                objAtPoint.put(toMap, RecursiveAnnotationMapper.mapString(objAtPoint.getString(toMap), maps));
            }
        }

        JSONObject data = obj.getJSONObject("data");

        for (String map : data.keySet()) {
            JSONObject dataInner = data.getJSONObject(map);

            for (String s : dataInner.keySet()) {
                JSONObject objAtPoint = dataInner.getJSONObject(s);

                for (String toMap : objAtPoint.keySet()) {
                    objAtPoint.put(toMap, RecursiveAnnotationMapper.mapString(objAtPoint.getString(toMap), maps));
                }
            }
        }

        return obj.toString(4);
    }

    private static void determineMappingMethod(String old) {
        JSONObject obj = new JSONObject(old);

        if(obj.has("package") && obj.has("refmap"))
            mappingMethod = false;
    }

    private static Map<String, byte[]> map(File modToRemap, File mapping, File intermediaryJar) throws IOException {
        TinyRemapper.Builder builder = TinyRemapper.newRemapper();
        builder.withMappings(TinyUtils.createTinyMappingProvider(mapping.toPath(), "intermediary", "named"));
        builder.withMappings(TinyUtils.createTinyMappingProvider(generateFabricLoaderMappings().toPath(), "fabric", "ballsack"));
        builder.ignoreConflicts(true);
        builder.threads(1);
        TinyRemapper remapper = builder.build();

        int classSize = 0;
        {
            try {
                JarFile files = new JarFile(modToRemap);
                for (JarEntry entry : Util.getEntries(files)) {
                    if(entry.getName().endsWith(".class"))
                        classSize++;
                }
                files.close();

                files = new JarFile(intermediaryJar);
                for (JarEntry entry : Util.getEntries(files)) {
                    if(entry.getName().endsWith(".class"))
                        classSize++;
                }
                files.close();
            } catch (IOException e) {
                classSize = 100000;
            }
        }

        Map<String, byte[]> finalFile = new HashMap<>();

        Map<String, byte[]> mappings = new HashMap<>();
        List<Path> files = new ArrayList<>();
        files.add(intermediaryJar.toPath());
        files.add(modToRemap.toPath());
        remapper.readInputs(files.toArray(Path[]::new));
        remapper.apply((s, bytes) -> {
            synchronized (new Object()) {
                mappings.put(s, bytes);
            }
        });

        if(classSize != 100000) {
            while(mappings.size() != classSize) {} //Wait For remapper to finish
            remapper.finish();
        }

        for (String s : mappings.keySet()) {
            byte[] bytes = mappings.get(s);
            if(bytes == null) continue;
            if(Application.modLoadingInformation.minecraftClasses().contains(s + ".class")) continue;

            finalFile.put(s + ".class", bytes);
        }

        return finalFile;
    }
}
