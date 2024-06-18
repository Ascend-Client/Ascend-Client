package io.github.betterclient.client.util.modremapper;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.DownloadedMinecraft;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.fabric.relocate.RelocatedClasses;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static java.lang.reflect.Modifier.isInterface;
import static java.lang.reflect.Modifier.isStatic;

public class ModRemapper {
    static boolean isCurrentModFSAPI;

    public static File remapMod(File modToRemap, boolean isBuiltin) throws IOException {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();
        DownloadedMinecraft version = Application.minecraft;

        ModLoadingInformation old = Application.modLoadingInformation;
        Application.modLoadingInformation = new ModLoadingInformation(old.minecraftClasses(), old.nonCustomMods(), old.state(), modToRemap);

        File remappedMod = new File(Application.remappedModsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
        if(isBuiltin) {
            remappedMod = new File(Application.remappedModsFolder, "builtin");
            Files.createDirectories(remappedMod.toPath());
            remappedMod = new File(remappedMod, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
        }
        if(remappedMod.exists() && !Application.doRemappingOfAlreadyRemappedMods) {
            if(Util.readAndClose(new FileInputStream(remappedMod)).length != 0)
                return remappedMod;
            else
                bridge.info("Found corrupted mod file, deleting and remapping");
        }

        bridge.info("Remapping mod " + modToRemap.getName());

        if(!remappedMod.delete()) {}
        if(!remappedMod.createNewFile()) {
            bridge.error("Failed to create file (?)");
        }

        Map<String, byte[]> finalFile = map(modToRemap, version);

        JarFile file = new JarFile(modToRemap);
        for (JarEntry entry : Util.getEntries(file)) {
            if(!entry.getName().endsWith(".class") && !entry.getName().equals("META-INF/MANIFEST.MF")) {
                InputStream is = file.getInputStream(entry);
                byte[] bites = is.readAllBytes();

                bites = modifyEntry(entry, bites);

                finalFile.put(entry.getName(), bites);
                is.close();
            }
        }

        Map<String, String> tinyMappings = new HashMap<>();
        populateMappings(tinyMappings, version);

        for (String s : new ArrayList<>(finalFile.keySet())) {
            byte[] bytes = finalFile.get(s);

            if(s.endsWith(".json")) {
                bytes = modifyRefmap(bytes, tinyMappings);
            } else if(s.endsWith(".lang")) {
                s = s.substring(0, s.lastIndexOf('.')) + ".json";
                bytes = convertLangToJSON(bytes);
            } else if(s.endsWith(".accesswidener")) {
                bytes = modifyAccessWidener(bytes, tinyMappings);
            } else if(s.endsWith(".class")) {
                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                boolean isMixin = detectMixin(node);

                for (FieldNode field : node.fields) {
                    mapShadowField(field, tinyMappings);
                }
                for (MethodNode method : node.methods) {
                    if(isMixin) {
                        if(method.visibleAnnotations != null) {
                            if(method.visibleAnnotations.isEmpty()) {
                                fixAccess(method, node);
                            }

                            for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                                mapShadowMethod(method, visibleAnnotation, tinyMappings);

                                fixAccess(method, visibleAnnotation);
                            }
                        } else {
                            fixAccess(method, node);
                        }
                    }

                    if(isMixin) {
                        for (AbstractInsnNode instruction : method.instructions) {
                            if(instruction instanceof MethodInsnNode min) {
                                mapInstruction(node, min, tinyMappings);
                            }

                            if(instruction instanceof FieldInsnNode fin) {
                                mapInstruction(node, fin, tinyMappings);
                            }
                        }
                    }
                }

                finallyModifyNode(node, tinyMappings);

                try {
                    ModIssueFixer.edit(node, modToRemap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                bytes = writer.toByteArray();
            } else {continue;}
            finalFile.put(s, bytes);
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

    private static void finallyModifyNode(ClassNode node, Map<String, String> mappings) {
        for (MethodNode method : node.methods) {
            if(method.name.startsWith("method_")) {
                method.name = mappings.getOrDefault(method.name, method.name);
            }

            for (AbstractInsnNode instruction : method.instructions) {
                if(instruction instanceof MethodInsnNode min) {
                    if(min.name.startsWith("method_")) {
                        min.name = mappings.getOrDefault(min.name, min.name);
                    }
                }

                if(instruction instanceof InvokeDynamicInsnNode idin) {
                    if(idin.name.startsWith("method_")) {
                        idin.name = mappings.getOrDefault(idin.name, idin.name);
                    }

                    if(idin.bsmArgs.length >= 3 && idin.bsmArgs[1] instanceof Handle h) {
                        if(h.getName().startsWith("method_")) {
                            idin.bsmArgs[1] = new Handle(h.getTag(), h.getOwner(), mappings.getOrDefault(h.getName(), h.getName()), h.getDesc(), h.getTag() == Opcodes.H_INVOKEINTERFACE);
                        }
                    }
                }
            }
        }
    }

    private static void fixAccess(MethodNode method, ClassNode node) {
        method.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")));

        if(isStatic(method.access) && !isInterface(node.access)) {
            method.access = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC;
        }
    }

    private static byte[] convertLangToJSON(byte[] bytes) {
        String source = new String(bytes);
        JSONObject obj = new JSONObject();

        for (String line : source.split("\n")) {
            if(!line.startsWith("#") && !line.isEmpty()) {
                String[] val = line.split("=");
                obj.put(val[0], val.length == 1 ? "" : val[1]);
            }
        }

        return obj.toString(4).getBytes();
    }

    private static void fixAccess(MethodNode method, AnnotationNode visibleAnnotation) {
        if(visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;")) {
            if(isStatic(method.access)) {
                method.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
            } else {
                method.access = Opcodes.ACC_PUBLIC;
            }
        }
    }

    private static void mapShadowMethod(MethodNode method, AnnotationNode visibleAnnotation, Map<String, String> tinyMappings) {
        if(visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;") || visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;")) {
            method.name = map(tinyMappings, method.name);
        }
    }

    private static void mapShadowField(FieldNode field, Map<String, String> tinyMappings) {
        if(field.visibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : field.visibleAnnotations) {
                if(visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                    field.name = map(tinyMappings, field.name);
                }
            }
        }
    }

    private static boolean detectMixin(ClassNode node) {
        if(node.invisibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : node.invisibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return true;
                }
            }
        }

        return false;
    }

    private static byte[] modifyAccessWidener(byte[] bytes, Map<String, String> tinyMappings) {
        String source = new String(bytes);

        List<String> finalSrc = new ArrayList<>();
        for (String s1 : source.split("\n")) {
            finalSrc.add(mapAccessWidener(tinyMappings, s1));
        }

        return String.join("\n", finalSrc).getBytes();
    }

    private static byte[] modifyRefmap(byte[] bytes, Map<String, String> tinyMappings) {
        String source = new String(bytes);
        source = String.join("\n", Arrays.stream(source.split("\n")).filter(s -> !s.contains("//")).toArray(String[]::new));
        JSONObject object = new JSONObject(source);

        if(object.has("package") && object.has("compatibilityLevel") && isCurrentModFSAPI && object.has("client")) {
            JSONArray client = object.getJSONArray("client");
            int index = 0;
            for (Object o : client.toList()) {
                if(o instanceof String str && str.equals("GameRendererMixin")) {
                    client.remove(index);
                }

                index++;
            }

            return object.toString(4).getBytes();
        }

        if(!object.has("mappings") && !object.has("data")) { return bytes; }

        JSONObject mappingsObject = object.getJSONObject("mappings");
        for (String name : mappingsObject.keySet()) {
            JSONObject toMap = mappingsObject.getJSONObject(name);
            for (String unMapped : toMap.keySet()) {
                toMap.put(unMapped, mapRefmap(tinyMappings, toMap.getString(unMapped)));
            }
        }

        JSONObject dataObject = object.getJSONObject("data");
        for (String s1 : dataObject.keySet()) {
            JSONObject actualData = dataObject.getJSONObject(s1);
            for (String name : actualData.keySet()) {
                JSONObject toMap = actualData.getJSONObject(name);
                for (String unMapped : toMap.keySet()) {
                    toMap.put(unMapped, mapRefmap(tinyMappings, toMap.getString(unMapped)));
                }
            }
        }

        return object.toString(5).getBytes();
    }

    private static void populateMappings(Map<String, String> tinyMappings, DownloadedMinecraft version) throws IOException {
        for (String line : Files.readAllLines(version.intermediaryToYarn().toPath())) {
            String[] a = line.split("\t");

            if(line.startsWith("CLASS\t")) {
                tinyMappings.put(a[1], a[2]);
            } else if(line.startsWith("FIELD\t")) {
                tinyMappings.put(a[3], a[4]);
            } else if(line.startsWith("METHOD\t")) {
                tinyMappings.put(a[3], a[4]);
            }
        }

        if(version.version().version().startsWith("1.16"))
            tinyMappings.put("method_31322", "changeFocus");
    }

    private static byte[] modifyEntry(JarEntry entry, byte[] bites) {
        if(entry.getName().endsWith(".json")) {
            String src = new String(bites);
            src = String.join("\n", Arrays.stream(src.split("\n")).filter(s -> !s.contains("//")).toArray(String[]::new));
            JSONObject obj = new JSONObject(src);

            if(obj.has("id")) {
                if(obj.getString("id").equals("fabric-screen-api-v1")) {
                    isCurrentModFSAPI = true;
                } else {
                    isCurrentModFSAPI = false;
                }
            } else if(obj.has("package") && obj.has("compatibilityLevel")) {
                src = String.join("\n", Arrays.stream(src.split("\n")).filter(s -> !s.contains("MixinMinecraft_NoAuthInDev")).toArray(String[]::new));
                obj = new JSONObject(src);

                obj.put("required", true);
                obj.put("minVersion", "0.8.5");
            }

            bites = obj.toString(4).getBytes();
        }

        return bites;
    }

    private static Map<String, byte[]> map(File modToRemap, DownloadedMinecraft version) throws IOException {
        TinyRemapper.Builder builder = TinyRemapper.newRemapper();
        builder.withMappings(TinyUtils.createTinyMappingProvider(version.intermediaryToYarn().toPath(), "intermediary", "named"));
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

                files = new JarFile(version.intermediaryJar());
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
        files.add(version.intermediaryJar().toPath());
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

    private static void mapInstruction(ClassNode container, AbstractInsnNode node, Map<String, String> mappings) throws IOException {
        String name = "";
        String owner = "";
        if(node instanceof FieldInsnNode fin) {
            name = fin.name;
            owner = fin.owner;
        } else if(node instanceof MethodInsnNode min) {
            name = min.name;
            owner = min.owner;
        }

        if(container.name.equals(owner)) {
            name = mappings.getOrDefault(name, name);
        }


        if(node instanceof FieldInsnNode fin) {
            fin.name = name;
        } else if(node instanceof MethodInsnNode min) {
            min.name = name;
        }
    }

    private static String map(Map<String, String> mappings, String toMap) {
        return mappings.getOrDefault(toMap, toMap);
    }

    private static String mapAccessWidener(Map<String, String> mappings, String toMap) {
        if(mappings.containsKey(toMap))
            return mappings.get(toMap);

        if(toMap.startsWith("accessible\t") || toMap.startsWith("transitive-accessible\t")) {
            String[] linee = toMap.split("\t");
            String className = mappings.getOrDefault(linee[2].replace('/', '.'), linee[2].replace('/', '.'));

            String start = linee[0] + "\t" + linee[1] + "\t" + className;
            switch (linee[1]) {
                case "class" -> {
                    return start;
                }
                case "field" -> {
                    String returnName = mapDesc(mappings, linee[4]);
                    return start + "\t" + mappings.getOrDefault(linee[3], linee[3]) + "\t" + returnName;
                }
                case "method" -> {
                    String desc = linee[4];
                    String parameters = desc.substring(0, desc.lastIndexOf(')') + 1);
                    List<String> methodParameters = parseMethodDescriptor(parameters + "V");
                    methodParameters.remove(methodParameters.get(methodParameters.size() - 1));
                    StringBuilder methodDesc = new StringBuilder();
                    for (String methodParameter : methodParameters) {
                        if (methodParameter.isEmpty())
                            continue;

                        if (methodParameter.charAt(0) == 'L') {
                            methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

                            if (mappings.containsKey(methodParameter)) {
                                methodParameter = mappings.get(methodParameter);
                            }

                            methodDesc.append("L").append(methodParameter).append(";");
                        } else if (methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
                            methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

                            if (mappings.containsKey(methodParameter)) {
                                methodParameter = mappings.get(methodParameter);
                            }

                            methodDesc.append("[").append("L").append(methodParameter).append(";");
                        } else {
                            methodDesc.append(methodParameter);
                        }
                    }
                    String returnClass = desc.substring(desc.lastIndexOf(')') + 1);
                    returnClass = mapDesc(mappings, returnClass);
                    return start + "\t" + mappings.getOrDefault(linee[3], linee[3]) + "\t(" + methodDesc + ")" + returnClass;
                }
                default -> {
                    return toMap;
                }
            }
        } else {
            return toMap;
        }
    }

    private static String mapDesc(Map<String, String> mappings, String returnClass) {
        if(returnClass.startsWith("L") && returnClass.endsWith(";")) {
            if(returnClass.contains("[")) {
                returnClass = returnClass.substring(2, returnClass.length() - 1);
                returnClass = mappings.getOrDefault(returnClass, returnClass);
                returnClass = "[L" + returnClass + ";";
            } else {
                returnClass = returnClass.substring(1, returnClass.length() - 1);
                returnClass = mappings.getOrDefault(returnClass, returnClass);
                returnClass = "L" + returnClass + ";";
            }

            return returnClass;
        }

        return returnClass;
    }

    private static String mapRefmap(Map<String, String> mappings, String toMap) {
        if(mappings.containsKey(toMap))
            return mappings.get(toMap);

        if(toMap.contains(":")) {
            return mapField(toMap, mappings);
        } else if(!toMap.contains(";") && !toMap.contains(":") && toMap.contains("(")) {
            return mapWithNoContainerClassMethodDontRemapDesc(toMap, mappings);
        } else if(!toMap.contains(";") && !toMap.contains(":")) {
            return mappings.getOrDefault(toMap, toMap);
        } else {
            if(toMap.startsWith("Lnet/") || toMap.startsWith("Lcom/") || (toMap.contains("method_") && toMap.split("\\(")[0].contains("L"))) {
                toMap = toMap.substring(1);

                String containerClass = toMap.split(";")[0];
                String substring = toMap.substring(toMap.indexOf(';') + 1, toMap.indexOf('('));
                if(mappings.containsKey(containerClass)) {
                    return mapContainerClassContainedKey(substring, toMap, mappings, containerClass);
                }

                return mapContainerClass_MethodName_Desc_Return(substring, toMap, mappings, containerClass);
            } else if(toMap.startsWith("L") && (!toMap.startsWith("Lnet/") && !toMap.startsWith("Lcom/")) && !toMap.contains("method_")) {
                String containerClass = toMap.split(";")[0];
                String substring = toMap.substring(toMap.indexOf(';') + 1, toMap.indexOf('('));

                return mapWithContainerClassMethodDontRemapMethodName(substring, toMap, mappings, containerClass.substring(1));
            } else {
                return mapWithNoContainerClassMethod(toMap, mappings);
            }
        }
    }

    private static String mapWithContainerClassMethodDontRemapMethodName(String substring, String toMap, Map<String, String> mappings, String containerClass) {
        StringBuilder methodDesc = new StringBuilder(toMap.substring(toMap.indexOf('(') + 1, toMap.indexOf(')')));
        List<String> methodParameters = parseMethodDescriptor("(" + methodDesc + ")V");
        methodParameters.remove(methodParameters.get(methodParameters.size() - 1));

        methodDesc = new StringBuilder();
        for (String methodParameter : methodParameters) {
            if(methodParameter.isEmpty())
                continue;

            if(methodParameter.charAt(0) == 'L') {
                methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("L").append(methodParameter).append(";");
            } else if(methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
                methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("[").append("L").append(methodParameter).append(";");
            } else {
                methodDesc.append(methodParameter);
            }
        }

        String methodReturn = toMap.substring(toMap.indexOf(')') + 1);
        if(methodReturn.charAt(0) == 'L') {
            methodReturn = methodReturn.substring(1, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "L" + methodReturn + ";";
        }

        if(methodReturn.charAt(0) == '[' && methodReturn.charAt(1) == 'L') {
            methodReturn = methodReturn.substring(2, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "[L" + methodReturn + ";";
        }

        return "L" + containerClass + ";" + substring + "(" + methodDesc + ")" + methodReturn;
    }

    private static String mapField(String toMap, Map<String, String> mappings) {
        String fieldName = toMap.split(":")[0];
        String desc = toMap.split(":")[1];

        if(fieldName.contains(";")) {
            fieldName = mapWithContainerClassField(fieldName, mappings);
        } else if(mappings.containsKey(fieldName)) {
            fieldName = mappings.get(fieldName);
        }

        if(desc.contains("L") && desc.contains(";")) {
            desc = mapDescField(desc, mappings);
        }

        return fieldName + ":" + desc;
    }

    private static String mapDescField(String desc, Map<String, String> mappings) {
        if(desc.charAt(0) == '[') {
            desc = desc.substring(2, desc.length() - 1);

            if(mappings.containsKey(desc)) {
                desc = mappings.get(desc);
            }

            return "[L" + desc + ";";
        } else {
            desc = desc.substring(1, desc.length() - 1);

            if(mappings.containsKey(desc)) {
                desc = mappings.get(desc);
            }

            return "L" + desc + ";";
        }
    }

    private static String mapWithContainerClassField(String fieldName, Map<String, String> mappings) {
        String className = fieldName.split(";")[0].substring(1);
        String field1Name = fieldName.split(";")[1];

        if(mappings.containsKey(className)) {
            className = mappings.get(className);
        }

        if(mappings.containsKey(field1Name)) {
            field1Name = mappings.get(field1Name);
        }

        return "L" + className + ";" + field1Name;
    }

    private static String mapWithNoContainerClassMethodDontRemapDesc(String toMap, Map<String, String> mappings) {
        String methodName = toMap.substring(0, toMap.indexOf('('));
        methodName = mappings.getOrDefault(methodName, methodName);

        String desc = toMap.substring(toMap.indexOf('(') + 1, toMap.lastIndexOf(')'));
        String methodReturn = toMap.substring(toMap.lastIndexOf(')') + 1);

        return methodName + "(" + desc +")" + methodReturn;
    }

    private static String mapWithNoContainerClassMethod(String toMap, Map<String, String> mappings) {
        String methodName = toMap.substring(0, toMap.indexOf('('));
        methodName = mappings.getOrDefault(methodName, methodName);

        StringBuilder methodDesc = new StringBuilder(toMap.substring(toMap.indexOf('(') + 1, toMap.indexOf(')')));
        List<String> methodParameters = parseMethodDescriptor("(" + methodDesc + ")V");
        methodParameters.remove(methodParameters.get(methodParameters.size() - 1));

        methodDesc = new StringBuilder();
        for (String methodParameter : methodParameters) {
            if(methodParameter.isEmpty())
                continue;

            if(methodParameter.charAt(0) == 'L') {
                methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("L").append(methodParameter).append(";");
            } else if(methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
                methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("[").append("L").append(methodParameter).append(";");
            } else {
                methodDesc.append(methodParameter);
            }
        }

        String methodReturn = toMap.substring(toMap.indexOf(')') + 1);
        if(methodReturn.charAt(0) == 'L') {
            methodReturn = methodReturn.substring(1, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "L" + methodReturn + ";";
        }

        if(methodReturn.charAt(0) == '[' && methodReturn.charAt(1) == 'L') {
            methodReturn = methodReturn.substring(2, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "[L" + methodReturn + ";";
        }

        return methodName + "(" + methodDesc + ")" + methodReturn;
    }

    private static String mapContainerClassContainedKey(String substring, String toMap, Map<String, String> mappings, String containerClass) {
        containerClass = mappings.get(containerClass);

        String methodName = substring;
        methodName = mappings.getOrDefault(methodName, "Default");
        try {
            if(!toMap.contains("(")) return map(mappings, toMap);

            if(toMap.indexOf('(') < (toMap.indexOf(";") + 1)) {
                return mapNoContainerClassRefmap(toMap, mappings);
            }

            if(methodName.equals("Default")) methodName = toMap.contains(";") ? toMap.substring(toMap.indexOf(";") + 1, toMap.indexOf('(')) : toMap;
        } catch (Exception e) {
            System.out.println(toMap + " (" + (toMap.indexOf(";") + 1) + ", " + toMap.indexOf('(') + ")");
            IBridge.getPreLaunch().error(e.toString());
        }

        StringBuilder methodDesc = new StringBuilder(toMap.substring(toMap.indexOf('(') + 1, toMap.indexOf(')')));
        List<String> methodParameters = parseMethodDescriptor("(" + methodDesc + ")V");
        methodParameters.remove(methodParameters.get(methodParameters.size() - 1));

        methodDesc = new StringBuilder();
        for (String methodParameter : methodParameters) {
            if(methodParameter.isEmpty())
                continue;

            mapMethodDesc(methodParameter, methodDesc, mappings);
        }

        String methodReturn = toMap.substring(toMap.indexOf(')') + 1);
        if(methodReturn.charAt(0) == 'L') {
            methodReturn = methodReturn.substring(1, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "L" + methodReturn + ";";
        }

        if(methodReturn.charAt(0) == '[' && methodReturn.charAt(1) == 'L') {
            methodReturn = methodReturn.substring(2, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "[L" + methodReturn + ";";
        }

        return "L" + containerClass + ";" + methodName + "(" + methodDesc + ")" + methodReturn;
    }

    private static void mapMethodDesc(String methodParameter, StringBuilder methodDesc, Map<String, String> mappings) {
        if(methodParameter.charAt(0) == 'L') {
            methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

            if(methodParameter.charAt(methodParameter.length() - 2) == '$') {
                char toReAdd = methodParameter.charAt(methodParameter.length() - 1);
                methodParameter = methodParameter.substring(0, methodParameter.indexOf('$'));

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodParameter = methodParameter + "$" + toReAdd;
            } else {
                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }
            }

            methodDesc.append("L").append(methodParameter).append(";");
        } else if(methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
            methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

            if(mappings.containsKey(methodParameter)) {
                methodParameter = mappings.get(methodParameter);
            }

            methodDesc.append("[").append("L").append(methodParameter).append(";");
        } else {
            methodDesc.append(methodParameter);
        }
    }

    public static String mapClassName(String className, Map<String, String> mappings) {
        if(className.charAt(className.length() - 2) == '$') {
            char toReAdd = className.charAt(className.length() - 1);
            className = className.substring(0, className.indexOf('$'));

            if(mappings.containsKey(className)) {
                className = mappings.get(className);
            }

            className = className + "$" + toReAdd;
        } else {
            if(mappings.containsKey(className)) {
                className = mappings.get(className);
            }
        }

        return className;
    }

    private static String mapNoContainerClassRefmap(String unMapped, Map<String, String> mappings) {
        String methodName = unMapped.substring(0, unMapped.indexOf('('));
        methodName = mappings.getOrDefault(methodName, methodName);

        StringBuilder methodDesc = new StringBuilder(unMapped.substring(unMapped.indexOf('(') + 1, unMapped.indexOf(')')));
        List<String> methodParameters = parseMethodDescriptor("(" + methodDesc + ")V");
        methodParameters.remove(methodParameters.get(methodParameters.size() - 1));

        methodDesc = new StringBuilder();
        for (String methodParameter : methodParameters) {
            if(methodParameter.isEmpty())
                continue;

            if(methodParameter.charAt(0) == 'L') {
                methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("L").append(methodParameter).append(";");
            } else if(methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
                methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("[").append("L").append(methodParameter).append(";");
            } else {
                methodDesc.append(methodParameter);
            }
        }

        String methodReturn = unMapped.substring(unMapped.indexOf(')') + 1);
        if(methodReturn.charAt(0) == 'L') {
            methodReturn = methodReturn.substring(1, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "L" + methodReturn + ";";
        }

        if(methodReturn.charAt(0) == '[' && methodReturn.charAt(1) == 'L') {
            methodReturn = methodReturn.substring(2, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "[L" + methodReturn + ";";
        }

        return methodName + "(" + methodDesc + ")" + methodReturn;
    }

    private static String mapContainerClass_MethodName_Desc_Return(String substring, String toMap, Map<String, String> mappings, String containerClass) {
        String methodName = substring;
        if(mappings.containsKey(methodName)) {
            methodName = mappings.get(methodName);
        }

        StringBuilder methodDesc = new StringBuilder(toMap.substring(toMap.indexOf('(') + 1, toMap.indexOf(')')));
        List<String> methodParameters = parseMethodDescriptor("(" + methodDesc + ")V");
        methodParameters.remove(methodParameters.get(methodParameters.size() - 1));

        methodDesc = new StringBuilder();
        for (String methodParameter : methodParameters) {
            if(methodParameter.isEmpty())
                continue;

            if(methodParameter.charAt(0) == 'L') {
                methodParameter = methodParameter.substring(1, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("L").append(methodParameter).append(";");
            } else if(methodParameter.charAt(0) == '[' && methodParameter.charAt(1) == 'L') {
                methodParameter = methodParameter.substring(2, methodParameter.length() - 1);

                if(mappings.containsKey(methodParameter)) {
                    methodParameter = mappings.get(methodParameter);
                }

                methodDesc.append("[").append("L").append(methodParameter).append(";");
            } else {
                methodDesc.append(methodParameter);
            }
        }

        String methodReturn = toMap.substring(toMap.indexOf(')') + 1);
        if(methodReturn.charAt(0) == 'L') {
            methodReturn = methodReturn.substring(1, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "L" + methodReturn + ";";
        }

        if(methodReturn.charAt(0) == '[' && methodReturn.charAt(1) == 'L') {
            methodReturn = methodReturn.substring(2, methodReturn.length() - 1);

            if(mappings.containsKey(methodReturn)) {
                methodReturn = mappings.get(methodReturn);
            }

            methodReturn = "[L" + methodReturn + ";";
        }

        return "L" + containerClass + ";" + methodName + "(" + methodDesc + ")" + methodReturn;
    }

    public static List<String> parseMethodDescriptor(String descriptor) {
        List<String> components = new ArrayList<>();
        if (descriptor != null && descriptor.startsWith("(")) {
            int index = 1;
            while (descriptor.charAt(index) != ')') {
                int endIndex = parseFieldType(descriptor, index);
                components.add(descriptor.substring(index, endIndex));
                index = endIndex;
            }
            int endIndex = parseFieldType(descriptor, index + 1);
            components.add(descriptor.substring(index + 1, endIndex));
        }
        return components;
    }

    private static int parseFieldType(String descriptor, int startIndex) {
        char type = descriptor.charAt(startIndex);
        switch (type) {
            case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 'V' -> {
                return startIndex + 1;
            }
            case 'L' -> {
                int endIndex = descriptor.indexOf(';', startIndex);
                return endIndex + 1;
            }
            case '[' -> {
                return parseFieldType(descriptor, startIndex + 1);
            }
            default -> throw new IllegalArgumentException("Invalid descriptor");
        }
    }

    private static File generateFabricLoaderMappings() throws IOException {
        File mappings = File.createTempFile("FabricLoader", ".tiny");
        List<String> classses = RelocatedClasses.getFabricClasses();
        List<String> lines = new ArrayList<>();
        lines.add("v1\tfabric\tballsack");
        for (String s : classses) {
            lines.add("CLASS\t" + s + "\t" + relocate(s));
        }
        FileOutputStream fos = new FileOutputStream(mappings);
        fos.write(String.join("\n", lines.toArray(String[]::new)).getBytes());
        fos.close();
        return mappings;
    }

    private static String relocate(String s) {
        return s
                .replace("net/fabricmc/api", "io/github/betterclient/fabric/relocate/api")
                .replace("net/fabricmc/loader", "io/github/betterclient/fabric/relocate/loader");
    }

    public static Map<String, String> generateMappings(DownloadedMinecraft version) throws IOException {
        Map<String, String> map = new HashMap<>();
        populateMappings(map, version);
        return map;
    }

    public static File remapInternalMod(File modToRemap, boolean builtin) throws IOException {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();
        DownloadedMinecraft version = Application.minecraft;

        ModLoadingInformation old = Application.modLoadingInformation;
        Application.modLoadingInformation = new ModLoadingInformation(old.minecraftClasses(), old.nonCustomMods(), old.state(), modToRemap);

        File remappedMod = new File(Application.remappedModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");
        if(builtin)
            remappedMod = new File(Application.remappedBuiltinModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remapped.jar");

        if(remappedMod.exists() && !Application.doRemappingOfAlreadyRemappedMods) {
            if(Util.readAndClose(new FileInputStream(remappedMod)).length != 0)
                return remappedMod;
            else
                bridge.info("Found corrupted mod file, deleting and remapping");
        }

        bridge.info("Remapping mod internal " + modToRemap.getName());

        if(!remappedMod.delete()) {}
        if(!remappedMod.createNewFile()) {
            bridge.error("Failed to create file (?)");
        }

        Map<String, byte[]> finalFile = map(modToRemap, version);

        JarFile file = new JarFile(modToRemap);
        for (JarEntry entry : Util.getEntries(file)) {
            if(!entry.getName().endsWith(".class") && !entry.getName().equals("META-INF/MANIFEST.MF")) {
                InputStream is = file.getInputStream(entry);
                byte[] bites = is.readAllBytes();

                bites = modifyEntry(entry, bites);

                finalFile.put(entry.getName(), bites);
                is.close();
            }
        }

        Map<String, String> tinyMappings = new HashMap<>();
        populateMappings(tinyMappings, version);

        for (String s : new ArrayList<>(finalFile.keySet())) {
            byte[] bytes = finalFile.get(s);

            if(s.endsWith(".json")) {
                bytes = modifyRefmap(bytes, tinyMappings);
            } else if(s.endsWith(".lang")) {
                s = s.substring(0, s.lastIndexOf('.')) + ".json";
                bytes = convertLangToJSON(bytes);
            } else if(s.endsWith(".accesswidener")) {
                bytes = modifyAccessWidener(bytes, tinyMappings);
            } else if(s.endsWith(".class")) {
                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                boolean isMixin = detectMixin(node);

                for (FieldNode field : node.fields) {
                    mapShadowField(field, tinyMappings);
                }
                for (MethodNode method : node.methods) {
                    if(isMixin) {
                        if(method.visibleAnnotations != null) {
                            if(method.visibleAnnotations.isEmpty()) {
                                fixAccess(method, node);
                            }

                            for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                                mapShadowMethod(method, visibleAnnotation, tinyMappings);

                                fixAccess(method, visibleAnnotation);
                            }
                        } else {
                            fixAccess(method, node);
                        }
                    }

                    if(isMixin) {
                        for (AbstractInsnNode instruction : method.instructions) {
                            if(instruction instanceof MethodInsnNode min) {
                                mapInstruction(node, min, tinyMappings);
                            }

                            if(instruction instanceof FieldInsnNode fin) {
                                mapInstruction(node, fin, tinyMappings);
                            }
                        }
                    }
                }

                finallyModifyNode(node, tinyMappings);

                try {
                    ModIssueFixer.edit(node, modToRemap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                bytes = writer.toByteArray();
            } else {continue;}
            finalFile.put(s, bytes);
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
}
