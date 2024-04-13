package io.github.betterclient.version.transformers;

import io.github.betterclient.quixotic.ClassTransformer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class RenderSystemTransformer implements ClassTransformer {
    @Override
    public byte[] transform(String s, byte[] bytes) {
        if(!s.equals("com.mojang.blaze3d.systems.RenderSystem"))
            return bytes;

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        {
            MethodNode enableScissor = new MethodNode();
            enableScissor.name = "enableScissor";
            enableScissor.desc = "(IIII)V";
            enableScissor.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
            InsnList instructions = new InsnList();
            try {
                ClassReader reader1 = new ClassReader(RenderSystemTransformer.class.getName());
                ClassNode node1 = new ClassNode();
                reader1.accept(node1, 0);

                for (MethodNode method : node1.methods) {
                    if(method.name.equals("enableScissorImpl")) {
                        instructions.add(method.instructions);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            enableScissor.instructions = instructions;
            node.methods.add(enableScissor);
        }

        {
            MethodNode disableScissor = new MethodNode();
            disableScissor.name = "disableScissor";
            disableScissor.desc = "()V";
            disableScissor.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
            InsnList instructions = new InsnList();
            try {
                ClassReader reader1 = new ClassReader(RenderSystemTransformer.class.getName());
                ClassNode node1 = new ClassNode();
                reader1.accept(node1, 0);

                for (MethodNode method : node1.methods) {
                    if(method.name.equals("disableScissorImpl")) {
                        instructions.add(method.instructions);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            disableScissor.instructions = instructions;
            node.methods.add(disableScissor);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static void enableScissorImpl(int x, int y, int endX, int endY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, y, endX, endY);
    }

    public static void disableScissorImpl() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
