package io.github.betterclient.client.util.modremapper.mixin.method;

import io.github.betterclient.client.util.modremapper.mixin.MethodNameMapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.Map;

public class OverwriteMapper extends MethodNameMapper {
    @Override
    public void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings) {
        super.mapMixin(n1, node, annotation, mappings);

        node.access = (Modifier.isStatic(node.access) ?  Opcodes.ACC_STATIC : 0) + Opcodes.ACC_PUBLIC;
    }

    @Override
    public boolean detect(AnnotationNode annotation) {
        return annotation.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;");
    }
}
