package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class YarnFix implements ClassTransformer {
    @Override
    public byte[] transform(String name, byte[] basicClass) {
        if(!name.startsWith("net.minecraft"))
            return basicClass;

        BetterClassNode bnode = new BetterClassNode(basicClass);
        ClassNode node  = bnode.getOrigin();
        node.access = fixAccess(node.access);
        node.fields.forEach((field) -> field.access = fixAccess(field.access));
        node.methods.forEach((method) -> method.access = fixAccess(method.access));
        node.innerClasses.forEach((clazz) -> clazz.access = fixAccess(clazz.access));

        return bnode.output();
    }

    private int fixAccess(int access) {
        if((access & 7) != 0x0002) { //private
            return (access & ~7) | 0x0001; //public
        }

        return access;
    }
}
