package io.github.betterclient.fabric.transformer;

import io.github.betterclient.client.util.modremapper.ProdFabricRemapper;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.fabric.relocate.RelocatedClasses;
import io.github.betterclient.quixotic.ClassTransformer;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class RelocationTransformer implements ClassTransformer {
    public static File fabricMaps;
    static {
        try {
            fabricMaps = ModRemapperUtility.generateFabricLoaderMappings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] transform(String className, byte[] unTransformedClass) {
        if (
                className.startsWith("io.github.betterclient.client.") ||
                className.startsWith("io.github.betterclient.fabric.") ||
                className.startsWith("io.github.betterclient.version.") ||
                className.startsWith("net.fabricmc.tinyremapper.") ||
                className.startsWith("net.fabricmc.mappingio.") ||
                className.startsWith("it.unimi.dsi.fastutil.") ||
                className.startsWith("com.intellij.rt.debugger.") || //lmfao
                unTransformedClass == null)
        {
            //Do className checks first so client classes aren't replaced
            return unTransformedClass;
        }

        if (ProdFabricRemapper.FABRIC_MAPPING_DATA.containsKey(className.replace(".", "/"))) {
            return ProdFabricRemapper.FABRIC_MAPPING_DATA.get(className.replace(".", "/"));
        }

        try {
            TinyRemapper remapper = TinyRemapper.newRemapper()
                    .threads(1)
                    .withMappings(TinyUtils.createTinyMappingProvider(fabricMaps.toPath(), "fabric", "ascend"))
                    .ignoreConflicts(true)
                    .checkPackageAccess(false)
                    .resolveMissing(false)
                    .build();
            Path f = Util.toFile(unTransformedClass, ".class");
            remapper.readInputs(f);
            AtomicReference<byte[]> transformed = new AtomicReference<>(null);
            remapper.apply((string, bytes) -> transformed.set(bytes));
            while (transformed.get() == null) {} //Wait finish
            remapper.finish();
            f.toFile().delete();
            return transformed.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void map(ClassNode node) throws IOException {
        node.superName = relocate(node.superName);
        node.interfaces.replaceAll(this::relocate);

        for (MethodNode method : node.methods) {
            method.desc = relocate(method.desc);

            if (method.localVariables != null) {
                for (LocalVariableNode localVariable : method.localVariables) {
                    localVariable.desc = relocate(localVariable.desc);
                    localVariable.signature = relocate(localVariable.signature);
                }
            }

            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode mn) {
                    mn.owner = relocate(mn.owner);
                    mn.desc = relocate(mn.desc);
                } else if(instruction instanceof FieldInsnNode fn) {
                    fn.owner = relocate(fn.owner);
                    fn.desc = relocate(fn.desc);
                }else if(instruction instanceof TypeInsnNode type) {
                    type.desc = relocate(type.desc);
                } else if (instruction instanceof InvokeDynamicInsnNode dyn) {
                    dyn.desc = relocate(dyn.desc);
                    ArrayList<Object> arr = mapObjects(dyn);
                    dyn.bsmArgs = arr.toArray();
                }
            }
        }

        for (FieldNode field : node.fields) {
            field.desc = relocate(field.desc);
        }
    }

    private ArrayList<Object> mapObjects(InvokeDynamicInsnNode dyn) {
        ArrayList<Object> arr = new ArrayList<>(Arrays.asList(dyn.bsmArgs));
        arr.replaceAll(o -> {
            if (o instanceof Type type) {
                return Type.getType(relocate(type.toString()));
            } else if (o instanceof Handle handle) {
                return new Handle(handle.getTag(), relocate(handle.getOwner()), handle.getName(), relocate(handle.getDesc()), handle.isInterface());
            }

            return o;
        });
        return arr;
    }

    private String relocate(String str) {
        if (str == null) return null;

        for (String fabricClass : RelocatedClasses.getFabricClasses()) {
            str = str.replaceAll(fabricClass, ModRemapperUtility.relocate(fabricClass));
        }
        return str;
    }
}
