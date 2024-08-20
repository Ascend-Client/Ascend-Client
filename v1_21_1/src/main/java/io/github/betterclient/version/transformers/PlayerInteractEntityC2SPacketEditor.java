package io.github.betterclient.version.transformers;

import io.github.betterclient.client.Application;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class PlayerInteractEntityC2SPacketEditor implements ClassTransformer {
    private static final String interactPack = Application.isDev ? "net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket" : "net.minecraft.class_2824";

    @Override
    public byte[] transform(String name, byte[] untransformedBytes) {
        if(!name.equals(interactPack)) return untransformedBytes;

        ClassReader reader = new ClassReader(untransformedBytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        MethodNode impl = new MethodNode();
        impl.name = "isAttack";
        impl.desc = "()Z";
        impl.access = ACC_PUBLIC;
        InsnList list = new InsnList();

        String ith = Application.isDev ? "net/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$InteractTypeHandler" : "net/minecraft/class_2824$class_5906";

        list.add(new FieldInsnNode(GETSTATIC, interactPack.replace(".", "/"), Application.isDev ? "ATTACK" : "field_29170", "L" + ith + ";"));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, interactPack.replace(".", "/"), Application.isDev ? "type" : "field_12871", "L" + ith + ";"));
        LabelNode l0 = new LabelNode();
        list.add(new JumpInsnNode(IF_ACMPNE, l0));
        list.add(new InsnNode(ICONST_1));
        LabelNode l1 = new LabelNode();
        list.add(new JumpInsnNode(GOTO, l1));
        list.add(l0);
        list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        list.add(new InsnNode(ICONST_0));
        list.add(l1);
        list.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER}));
        list.add(new InsnNode(IRETURN));

        impl.instructions = list;
        node.methods.add(impl);
        node.interfaces.add("io/github/betterclient/version/access/PlayerInteractEntityC2SPacketAccessor");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
