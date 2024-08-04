package io.github.betterclient.client.util.modremapper.utility;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.FabricLoader;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * fixes issues in replaymod and iris
 * <p>
 * these issues exist in the fabric version of the mods aswell
 */
public class ModIssueFixer {
    public static void edit(ClassNode node, File currentMod) throws Exception {
        if(Application.minecraft.version().version() == MinecraftVersion.Version.V1_19_4)
            edit_1_19_4(node);

        if(Application.minecraft.version().version() != MinecraftVersion.Version.COMBAT_TEST_8C) return;

        if(node.name.equals("com/replaymod/core/versions/MCVer") && FabricLoader.getInstance().getModName(currentMod).equals("Replay Mod")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals("asMc")) {
                    InsnList toInject = new InsnList();

                    /*Basically adds:
                    if(passedargument == CONFIGURATION) {
                        passedargument = PLAY;
                    }
                     */

                    toInject.add(new VarInsnNode(ALOAD, 0));
                    toInject.add(new FieldInsnNode(GETSTATIC, "com/replaymod/replaystudio/lib/viaversion/api/protocol/packet/State", "CONFIGURATION", "Lcom/replaymod/replaystudio/lib/viaversion/api/protocol/packet/State;"));
                    LabelNode l0 = new LabelNode();
                    toInject.add(new JumpInsnNode(IF_ACMPNE, l0));
                    toInject.add(new FieldInsnNode(GETSTATIC, "com/replaymod/replaystudio/lib/viaversion/api/protocol/packet/State", "PLAY", "Lcom/replaymod/replaystudio/lib/viaversion/api/protocol/packet/State;"));
                    toInject.add(new VarInsnNode(ASTORE, 0));
                    toInject.add(l0);
                    toInject.add(new FrameNode(F_SAME, 0, null, 0, null));

                    method.instructions.insert(toInject);
                }
            }
        }

        if(node.name.equals("net/coderbot/iris/gui/screen/ShaderPackScreen") && FabricLoader.getInstance().getModName(currentMod).equals("Iris")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals("init")) {
                    List<AbstractInsnNode> toRemove = getRemovalNodes(method);

                    toRemove.forEach(method.instructions::remove);
                }
            }
        }
    }

    private static void edit_1_19_4(ClassNode node) {
        if(node.name.equals("net/coderbot/iris/compat/sodium/mixin/vertex_format/entity/MixinEntityRenderDispatcher")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals("renderShadowPart")) {
                    method.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")));
                }
            }
        }

        if(!node.name.equals("net/fabricmc/fabric/mixin/entity/event/LivingEntityMixin")) return;
        node.methods.removeIf(method -> method.name.equals("onGetSleepingDirection"));
    }

    private static List<AbstractInsnNode> getRemovalNodes(MethodNode method) {
        List<AbstractInsnNode> toRemove = new ArrayList<>();

        for (AbstractInsnNode instruction : method.instructions) {
            if(instruction instanceof MethodInsnNode min && min.getOpcode() == INVOKEVIRTUAL && min.name.equals("method_31322")) {
                AbstractInsnNode current = instruction;
                do {
                    toRemove.add(current);
                } while ((current = current.getPrevious()).getOpcode() != ALOAD);
                toRemove.add(current);
            }
        }
        return toRemove;
    }
}
