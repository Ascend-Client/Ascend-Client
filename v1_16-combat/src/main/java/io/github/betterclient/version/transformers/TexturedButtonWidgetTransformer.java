package io.github.betterclient.version.transformers;

import io.github.betterclient.client.Application;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class TexturedButtonWidgetTransformer implements ClassTransformer {
    @Override
    public byte[] transform(String s, byte[] bytes) {
        if(!s.equals("net.minecraft.client.gui.widget.TexturedButtonWidget") && !s.equals("net.minecraft.class_344"))
            return bytes;

        String identifier = Application.isDev ? "net/minecraft/util/Identifier" : "net/minecraft/class_2960";
        String pressAction = Application.isDev ? "net/minecraft/client/gui/widget/ButtonWidget$PressAction" : "net/minecraft/class_4185$class_4241";
        String toolTipSupplier = Application.isDev ? "net/minecraft/client/gui/widget/ButtonWidget$TooltipSupplier" : "net/minecraft/class_4185$class_5316";
        String text = Application.isDev ? "net/minecraft/text/Text" : "net/minecraft/class_2561";
        String texturedButton = Application.isDev ? "net/minecraft/client/gui/widget/TexturedButtonWidget" : "net/minecraft/class_344";

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        MethodNode method = new MethodNode();
        method.name = "<init>";
        method.desc = "(IIIIIIIL" + identifier + ";IIL" + pressAction + ";L" + toolTipSupplier + ";L" + text + ";)V";
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
        insn.add(new MethodInsnNode(INVOKESPECIAL, texturedButton, "<init>", "(IIIIIIIL" + identifier + ";IIL" + pressAction + ";L" + text + ";)V", false));
        insn.add(new InsnNode(RETURN));

        method.instructions = insn;
        node.methods.add(method);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
