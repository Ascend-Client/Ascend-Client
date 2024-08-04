package io.github.betterclient.client.util.modremapper.mixin;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class ShadowMapper extends MethodNameMapper {
    public void mapMixin(FieldNode node, Map<String, String> mappings) {
        node.name = mappings.getOrDefault(node.name, node.name);
    }

    @Override
    public boolean detect(AnnotationNode annotation) {
        return annotation.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;");
    }
}
