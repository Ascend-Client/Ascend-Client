package io.github.betterclient.fabric.transformer;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class RedirectFabricCalls implements ClassTransformer {
    @Override
    public byte[] transform(String className, byte[] classFileBuffer) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classFileBuffer);
        classReader.accept(classNode, 0);
        boolean changedAnything = false;

        for (MethodNode method : classNode.methods) {
            InsnList instructions = method.instructions;

            for (AbstractInsnNode insnNode : instructions.toArray()) {
                if (insnNode instanceof MethodInsnNode methodInsnNode) {
                    if (methodInsnNode.owner.startsWith("net/fabricmc/loader")) {
                        changedAnything = true;
                        methodInsnNode.owner = methodInsnNode.owner.replace("net/fabricmc/loader", "io/github/betterclient/fabric/loader");
                    }
                } else if (insnNode instanceof FieldInsnNode fieldInsnNode) {
                    if (fieldInsnNode.owner.startsWith("net/fabricmc/loader")) {
                        changedAnything = true;
                        fieldInsnNode.owner = fieldInsnNode.owner.replace("net/fabricmc/loader", "io/github/betterclient/fabric/loader");
                    }
                }
            }
        }

        if(!changedAnything)
            return classFileBuffer;

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
