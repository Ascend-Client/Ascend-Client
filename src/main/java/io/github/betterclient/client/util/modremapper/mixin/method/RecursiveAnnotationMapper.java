package io.github.betterclient.client.util.modremapper.mixin.method;

import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RecursiveAnnotationMapper implements MixinMethodMapper {
    private static String register(String s) {
        return "L" + s.replace(".", "/") + ";";
    }

    private static final List<String> TO_MAP = List.of(
            register("org.spongepowered.asm.mixin.injection.Inject"),
            register("org.spongepowered.asm.mixin.injection.ModifyVariable"),
            register("org.spongepowered.asm.mixin.injection.ModifyArg"),
            register("org.spongepowered.asm.mixin.injection.ModifyArgs"),
            register("org.spongepowered.asm.mixin.injection.Redirect"),
            register("com.llamalad7.mixinextras.injector.v2.WrapWithCondition"),
            register("com.llamalad7.mixinextras.injector.WrapWithCondition"),
            register("com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation")
    );

    @Override
    public void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings) {
        recursiveMap(annotation, mappings);
    }

    @Override
    public boolean detect(AnnotationNode annotation) {
        return TO_MAP.contains(annotation.desc);
    }

    private void recursiveMap(AnnotationNode node, Map<String, String> mappings) {
        int index = -1;
        for (Object value : node.values) {
            index++;
            if(index % 2 == 0)
                continue;

            if(value instanceof Boolean) continue;

            if(value instanceof List<?> alist) {
                if(alist.getFirst() instanceof String) {
                    List<String> list = (List<String>) alist;
                    int i = 0;
                    for (String o : list) {
                        o = mapString(o, mappings);

                        list.set(i, o);
                        i++;
                    }
                } else if(alist.getFirst() instanceof AnnotationNode) {
                    List<AnnotationNode> list = (List<AnnotationNode>) alist;
                    for (AnnotationNode o : list) {
                        this.recursiveMap(o, mappings);
                    }
                }

                continue;
            }

            if(value instanceof String str) {
                node.values.set(index, mapString(str, mappings));
                continue;
            }

            if(value instanceof AnnotationNode anode) {
                this.recursiveMap(anode, mappings);
            }

            if(value instanceof String[] strarr) {
                int i = 0;
                for (String o : strarr) {
                    o = mapString(o, mappings);
                    strarr[i] = o;
                    i++;
                }
            }
        }
    }

    public static String mapString(String o, Map<String, String> mappings) {
        try {
            if(o.contains("(")) {
                if(o.startsWith("L")) {
                    o = mapMethodNameWithOwner(o, mappings);
                } else {
                    o = mapMethodName(o, mappings);
                }
            } else if(o.contains(":") && !o.contains(" ")) {
                if(o.contains(";") && o.indexOf(";") < o.indexOf(":")) {
                    o = mapFieldName(o, mappings);
                } else {
                    o = mapFieldNameWithoutOwner(o, mappings);
                }
            } else {
                o = mappings.getOrDefault(o, o);
            }
        } catch (Exception e) {
            System.out.println("Offending string: " + o);
            throw new RuntimeException(e);
        }

        return o;
    }

    private static String mapFieldNameWithoutOwner(String toMap, Map<String, String> mappings) {
        String fieldName = toMap.split(":")[0];
        String fieldDesc = toMap.split(":")[1];

        boolean isArray = fieldDesc.contains("[");

        if(fieldDesc.charAt(0) == 'L' || (fieldDesc.charAt(0) == '[') && fieldDesc.charAt(1) == 'L') {
            fieldDesc = fieldDesc.substring(1 + (isArray ? 1 : 0), fieldDesc.length() - 1);
        } else if(fieldDesc.charAt(0) == '[' && fieldDesc.charAt(0) != 'L') {
            fieldName = mappings.getOrDefault(fieldName, fieldName);
            return fieldName + ":" + (isArray ? "[" : "") + fieldDesc;
        } else if(fieldDesc.charAt(0) != 'L') {
            fieldName = mappings.getOrDefault(fieldName, fieldName);
            return fieldName + ":" + (isArray ? "[" : "") + fieldDesc;
        }

        fieldName = mappings.getOrDefault(fieldName, fieldName);
        fieldDesc = mappings.getOrDefault(fieldDesc, fieldDesc);

        return fieldName + ":" + (isArray ? "[L" : "L") + fieldDesc + ";";
    }

    private static String mapMethodNameWithOwner(String o, Map<String, String> mappings) {
        String owner = o.substring(1, o.indexOf(";"));
        String rest = o.substring(o.indexOf(";") + 1);

        owner = "L" + mappings.getOrDefault(owner, owner) + ";";

        return owner + mapMethodName(rest, mappings);
    }

    private static String mapFieldName(String toMap, Map<String, String> mappings) {
        String[] split0 = toMap.split(":");
        String[] split1 = split0[0].split(";");

        String ownerClass = split1[0];
        String fieldName = split1[1];
        String fieldDesc = split0[1];

        boolean isArray = fieldDesc.contains("[");
        ownerClass = ownerClass.substring(1);

        if(fieldDesc.charAt(0) == 'L' || (fieldDesc.charAt(0) == '[') && fieldDesc.charAt(1) == 'L') {
            fieldDesc = fieldDesc.substring(1 + (isArray ? 1 : 0), fieldDesc.length() - 1);
        } else if(fieldDesc.charAt(0) == '[' && fieldDesc.charAt(0) != 'L') {
            ownerClass = mappings.getOrDefault(ownerClass, ownerClass);
            fieldName = mappings.getOrDefault(fieldName, fieldName);

            return "L" + ownerClass + ";" + fieldName + ":" + fieldDesc;
        } else if(fieldDesc.charAt(0) != 'L') {
            ownerClass = mappings.getOrDefault(ownerClass, ownerClass);
            fieldName = mappings.getOrDefault(fieldName, fieldName);

            return "L" + ownerClass + ";" + fieldName + ":" + (isArray ? "[" : "") + fieldDesc;
        }


        ownerClass = mappings.getOrDefault(ownerClass, ownerClass);
        fieldName = mappings.getOrDefault(fieldName, fieldName);
        fieldDesc = mappings.getOrDefault(fieldDesc, fieldDesc);

        return "L" + ownerClass + ";" + fieldName + ":" + (isArray ? "[L" : "L") + fieldDesc + ";";
    }

    private static String mapMethodName(String toMap, Map<String, String> mappings) {
        String methodName = toMap.split("\\(")[0];
        String desc = "(" + toMap.split("\\(")[1];
        List<String> signature = ModRemapperUtility.parseMethodDescriptor(desc);

        int i = 0;
        for (String s : new ArrayList<>(signature)) {
            if(s.startsWith("L") && s.endsWith(";")) {
                String totoMap = s.substring(1, s.length() - 1);

                totoMap = mappings.getOrDefault(totoMap, totoMap);

                signature.set(i, "L" + totoMap + ";");
            }

            if(s.startsWith("[L") && s.endsWith(";")) {
                String totoMap = s.substring(2, s.length() - 1);

                totoMap = mappings.getOrDefault(totoMap, totoMap);

                signature.set(i, "[L" + totoMap + ";");
            }

            i++;
        }

        if(methodName.startsWith("method_"))
            methodName = mappings.getOrDefault(methodName, methodName);

        desc = ModRemapperUtility.reconstructSignature(signature);

        return methodName + desc;
    }
}
