package io.github.betterclient.fabric.transformer;

import io.github.betterclient.client.asm.BetterClassNode;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains class specific access fixes that are breaking minecraft in prod
 */
public class PrivateAccessTransformer implements ClassTransformer {
    public List<String> transforming = new ArrayList<>(List.of(
            "net.minecraft.client.render.Frustum",
            "net.minecraft.client.texture.Sprite$Interpolation"));

    @Override
    public byte[] transform(String className, byte[] classFileBuffer) {
        if(!transforming.contains(className))
            return classFileBuffer;

        BetterClassNode clazz = new BetterClassNode(classFileBuffer);
        MethodNode node = null;

        if(className.equals(transforming.get(0))) {
            node = clazz.getMethod("isVisible", "(DDDDDD)Z").getOrigin();
        }
        if(className.equals(transforming.get(1))) {
            node = clazz.getMethod("apply", "()V").getOrigin();
        }

        if(node == null)
            return classFileBuffer;

        node.access = Opcodes.ACC_PUBLIC;

        return clazz.output();
    }
}
