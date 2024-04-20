package io.github.betterclient.version.transformers;

import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class TexturedButtonWidgetTransformer implements ClassTransformer {
    @Override
    public byte[] transform(String s, byte[] bytes) {
        if(!s.equals("net.minecraft.client.gui.widget.TexturedButtonWidget"))
            return bytes;

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        MethodNode method = new MethodNode();
        method.name = "<init>";
        method.desc = "(IIIIIIILnet/minecraft/util/Identifier;IILnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Lnet/minecraft/client/gui/widget/ButtonWidget$TooltipSupplier;Lnet/minecraft/text/Text;)V";
        method.access = ACC_PUBLIC;
        InsnList insn = new InsnList();

        insn.add(new VarInsnNode(ALOAD, 0));

        //load all arguments
        insn.add(new VarInsnNode(ILOAD, 1));
        insn.add(new VarInsnNode(ILOAD, 2));
        insn.add(new VarInsnNode(ILOAD, 3));
        insn.add(new VarInsnNode(ILOAD, 4));
        insn.add(new VarInsnNode(ILOAD, 5));
        insn.add(new VarInsnNode(ILOAD, 6));
        insn.add(new VarInsnNode(ILOAD, 7));
        insn.add(new VarInsnNode(ALOAD, 8));
        insn.add(new VarInsnNode(ILOAD, 9));
        insn.add(new VarInsnNode(ILOAD, 10));
        insn.add(new VarInsnNode(ALOAD, 11));
        insn.add(new VarInsnNode(ALOAD, 13));

        //call function
        insn.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/client/gui/widget/TexturedButtonWidget", "<init>", "(IIIIIIILnet/minecraft/util/Identifier;IILnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Lnet/minecraft/text/Text;)V", false));
        insn.add(new InsnNode(RETURN));

        method.instructions = insn;
        node.methods.add(method);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
