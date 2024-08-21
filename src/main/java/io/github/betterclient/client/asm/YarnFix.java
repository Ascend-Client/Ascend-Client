package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import static java.lang.reflect.Modifier.*;

public class YarnFix implements ClassTransformer {
    @Override
    public byte[] transform(String name, byte[] basicClass) {
        if(basicClass == null)
            return null;

        if(basicClass.length == 0)
            return new byte[0];

        if(!name.startsWith("net.minecraft") && !name.startsWith("com.mojang.blaze3d."))
            return basicClass;

        BetterClassNode bnode = new BetterClassNode(basicClass);

        fixAccess(bnode.getOrigin());

        return bnode.output();
    }

    private static int fixAccess(int access) {
        if ((access & 0x7) != Opcodes.ACC_PRIVATE) {
            return (access & (~0x7)) | Opcodes.ACC_PUBLIC;
        } else if ((access & 0x7) != Opcodes.ACC_PROTECTED) {
            return (access & (~0x7)) | Opcodes.ACC_PUBLIC;
        } else {
            return access;
        }
    }

    private static int fixFieldAccess(ClassNode node, int access) {
        if (isFinal(access) && !isInterface(node.access)) {
            access &= ~Opcodes.ACC_FINAL;
        }

        return fixAccess(access);
    }

    private static int fixMethodAccess(int access) {
        if (isFinal(access)) {
            access &= ~Opcodes.ACC_FINAL;
        }

        return fixAccess(access);
    }


    public void fixAccess(ClassNode node) {
        if(isPublic(node.access) && isFinal(node.access) && !isInterface(node.access) && (node.name.startsWith("net/minecraft/")) && !node.superName.equals("java/lang/Enum")) {
            node.access = Opcodes.ACC_PUBLIC;
        }

        node.access = fixAccess(node.access);
        node.fields.forEach((field) -> field.access = fixFieldAccess(node, field.access));
        node.methods.forEach((method) -> method.access = fixMethodAccess(method.access));
        node.innerClasses.forEach(clazz -> clazz.access = fixAccess(clazz.access));
    }
}
