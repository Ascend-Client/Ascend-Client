package io.github.betterclient.client.util.modremapper.mixin;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public abstract class MethodNameMapper implements MixinMethodMapper {
    @Override
    public void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings) {
        node.name = mappings.getOrDefault(node.name, node.name);
    }
}
