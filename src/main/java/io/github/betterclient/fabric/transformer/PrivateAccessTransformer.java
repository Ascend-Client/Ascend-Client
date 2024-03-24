package io.github.betterclient.fabric.transformer;

import io.github.betterclient.client.asm.BetterClassNode;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains class specific access fixes that are breaking minecraft in prod
 */
public class PrivateAccessTransformer implements ClassTransformer {
    public List<String> transforming = new ArrayList<>(List.of(
            "com.mojang.blaze3d.platform.GlStateManager"
    ));

    @Override
    public byte[] transform(String className, byte[] classFileBuffer) {
        if(!className.equals("com.mojang.blaze3d.platform.GlStateManager")) {
            if(!className.equals("net.minecraft.client.render.chunk.ChunkOcclusionData"))
                return classFileBuffer;

            BetterClassNode clazz = new BetterClassNode(classFileBuffer);

            clazz.getField("visibility").access = Opcodes.ACC_PUBLIC;

            return clazz.output();
        }


        BetterClassNode clazz = new BetterClassNode(classFileBuffer);

        clazz.getField("TEXTURES").access = Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC;

        return clazz.output();
    }
}
