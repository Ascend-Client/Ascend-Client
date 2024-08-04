package io.github.betterclient.client.util.modremapper.mixin.method;

import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class InvokerMapper implements MixinMethodMapper {
    @Override
    public void mapMixin(ClassNode n1, MethodNode node, AnnotationNode annotation, Map<String, String> mappings) {
        String old = node.name;
        if(node.name.startsWith("invoke")) {
            String nameOfAccessed = node.name.toLowerCase().replace("invoke", "");
            node.name = "invoke" + ModRemapperUtility.capitalize(mappings.getOrDefault(nameOfAccessed, nameOfAccessed));
        } else {
            node.name = mappings.getOrDefault(node.name, node.name);
        }

        allMappedNames.put(old, n1.name + "-" + node.name);

        if(annotation.values == null) return;
        String name = (String) annotation.values.get(1);
        name = mappings.getOrDefault(name, name);
        annotation.values.set(1, name);
    }

    @Override
    public boolean detect(AnnotationNode annotation) {
        return annotation.desc.equals("Lorg/spongepowered/asm/mixin/gen/Invoker;");
    }
}
