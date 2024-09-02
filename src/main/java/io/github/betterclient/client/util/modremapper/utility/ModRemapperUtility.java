package io.github.betterclient.client.util.modremapper.utility;

import io.github.betterclient.client.util.downloader.DownloadedMinecraft;
import io.github.betterclient.fabric.relocate.RelocatedClasses;
import org.json.JSONObject;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModRemapperUtility {
    public static Map<String, String> generateMappings(DownloadedMinecraft version) throws IOException {
        Map<String, String> map = new HashMap<>();
        populateMappings(map, version.intermediaryToYarn());
        return map;
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

    public static boolean detectMixin(ClassNode node) {
        if(node.visibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : node.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return true;
                }
            }
        }

        if(node.invisibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : node.invisibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static File generateFabricLoaderMappings() throws IOException {
        File mappings = File.createTempFile("FabricLoader", ".tiny");
        List<String> classses = RelocatedClasses.getFabricClasses();
        List<String> lines = new ArrayList<>();
        lines.add("v1\tfabric\tascend");
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
                    methodParameters.remove(methodParameters.getLast());
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

    public static void populateMappings(Map<String, String> tinyMappings, File intermediaryToYarn) throws IOException {
        for (String line : Files.readAllLines(intermediaryToYarn.toPath())) {
            String[] a = line.split("\t");

            if(line.startsWith("CLASS\t")) {
                tinyMappings.put(a[1], a[2]);
            } else if(line.startsWith("FIELD\t")) {
                tinyMappings.put(a[3], a[4]);
            } else if(line.startsWith("METHOD\t")) {
                tinyMappings.put(a[3], a[4]);
            }
        }
    }

    public static byte[] convertLangToJSON(byte[] bytes) {
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

    public static byte[] modifyAccessWidener(byte[] bytes, Map<String, String> tinyMappings) {
        String source = new String(bytes);

        List<String> finalSrc = new ArrayList<>();
        for (String s1 : source.split("\n")) {
            finalSrc.add(mapAccessWidener(tinyMappings, s1));
        }

        return String.join("\n", finalSrc).getBytes();
    }

    public static String reconstructSignature(List<String> signatureList) {
        List<String> withoutLast = new ArrayList<>(signatureList);
        withoutLast.removeLast();

        StringBuilder sb = new StringBuilder("(");
        for (String s : withoutLast) {
            sb.append(s);
        }
        sb.append(")");
        sb.append(signatureList.getLast());
        return sb.toString();
    }

    public static String capitalize(String string) {
        if(string.contains(" ")) {
            String[] strE = string.split(" ");
            List<String> strEL = new ArrayList<>();
            for(String strELR : strE) {
                if(strELR.length() >= 2) {
                    strEL.add(strELR.toUpperCase().charAt(0) + strELR.toLowerCase().substring(1));
                } else {
                    strEL.add(strELR);
                }
            }
            string = String.join(" ", strEL.toArray(CharSequence[]::new));
        } else {
            string = string.toUpperCase().charAt(0) + string.substring(1);
        }

        return string;
    }

    public static String getDetectMixin(ClassNode node) {
        if(node.visibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : node.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    return (String) visibleAnnotation.values.get(1);
                }
            }
        }

        if(node.invisibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : node.invisibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    Object obj = visibleAnnotation.values.get(1);
                    if(obj instanceof ArrayList<?> alist) {
                        Object a = alist.getFirst();

                        if(a instanceof Type type)
                            return type.getDescriptor();
                        else if(a instanceof String alol)
                            return alol;
                    } else if(obj instanceof String alol) {
                        return alol;
                    }
                }
            }
        }

        return "";
    }

    public static String getMixinTarget(MethodNode method) {
        for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
            if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                boolean isThisOne = false;
                for (Object value : visibleAnnotation.values) {
                    if(isThisOne) {
                        if(value instanceof ArrayList<?> arrayList) {
                            return (String) arrayList.getFirst();
                        } else {
                            return (String) value;
                        }
                    }

                    if(value.equals("method")) {
                        isThisOne = true;
                    }
                }
            }
        }

        return "";
    }
}
