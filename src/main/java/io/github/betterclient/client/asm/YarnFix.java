package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

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

    private int fixAccess(int access) {
        if((access & 7) != Opcodes.ACC_PRIVATE) { //private
            return (access & ~7) | Opcodes.ACC_PUBLIC; //public
        }

        return access;
    }

    public void fixAccess(ClassNode node) {
        node.access = fixAccess(node.access);
        node.fields.forEach((field) -> field.access = fixAccess(field.access));
        node.methods.forEach((method) -> method.access = fixAccess(method.access));
        node.innerClasses.forEach(clazz -> clazz.access = fixAccess(clazz.access));
    }
}
