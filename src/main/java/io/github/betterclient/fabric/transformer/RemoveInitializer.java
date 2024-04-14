package io.github.betterclient.fabric.transformer;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class RemoveInitializer implements ClassTransformer {
    @Override
    public byte[] transform(String s, byte[] bytes) {
        if(bytes == null) return null;

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for (FieldNode field : node.fields) {
            if(field.desc.contains("net/fabricmc/") && (field.desc.contains("Initializer") || field.desc.contains("Entrypoint"))) {
                field.desc = "Ljava/lang/Runnable;";
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
