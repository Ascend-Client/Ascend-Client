package io.github.betterclient.client.util.modremapper.mixin;

import io.github.betterclient.client.util.modremapper.mixin.method.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MixinMethodMapper {
    UniqueMapper UNIQUE_MAPPER = new UniqueMapper();
    ShadowMapper SHADOW_MAPPER = new ShadowMapper();
    OverwriteMapper OVERWRITE_MAPPER = new OverwriteMapper();
    AccessorMapper ACCESSOR_MAPPER = new AccessorMapper();
    InvokerMapper INVOKER_MAPPER = new InvokerMapper();
    RecursiveAnnotationMapper RECURSIVE_ANNOTATION_MAPPER = new RecursiveAnnotationMapper();

    List<MixinMethodMapper> MAPPERS = List.of(
            UNIQUE_MAPPER, OVERWRITE_MAPPER, SHADOW_MAPPER,
            ACCESSOR_MAPPER, INVOKER_MAPPER, RECURSIVE_ANNOTATION_MAPPER
    );
    Map<String, String> allMappedNames = new HashMap<>();

    static void mapAll(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> tinyMappings) {
        for (MixinMethodMapper mapper : MAPPERS) {
            if(mapper.detect(annotation)) {
                mapper.mapMixin(n1, node, annotation, tinyMappings);
                break; //Only one mapper allowed
            }
        }
    }

    void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings);
    boolean detect(AnnotationNode annotation);
}
