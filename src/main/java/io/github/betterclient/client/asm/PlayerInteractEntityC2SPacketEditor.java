package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class PlayerInteractEntityC2SPacketEditor implements ClassTransformer {
    @Override
    public byte[] transform(String name, byte[] untransformedBytes) {
        if(!name.equals("net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket")) return untransformedBytes;

        ClassReader reader = new ClassReader(untransformedBytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        MethodNode impl = new MethodNode();
        impl.name = "isAttack";
        impl.desc = "()Z";
        impl.access = ACC_PUBLIC;
        InsnList list = new InsnList();

        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket", "ATTACK", "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$InteractTypeHandler;"));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket", "type", "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$InteractTypeHandler;"));
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
        node.interfaces.add("io/github/betterclient/client/access/PlayerInteractEntityC2SPacketAccessor");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
