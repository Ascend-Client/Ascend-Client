package io.github.betterclient.client.util.modremapper.mixin.method;

import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class UniqueMapper implements MixinMethodMapper {
    @Override
    public void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings) {
        //skip unique
    }

    @Override
    public boolean detect(AnnotationNode annotation) {
        return annotation.desc.equals("Lorg/spongepowered/asm/mixin/Unique;");
    }
}
