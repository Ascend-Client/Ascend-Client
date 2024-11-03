package io.github.betterclient.version;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class Version {
    public static IBridge bridge;
    public static IBridge.KeyStorage keys = new IBridge.KeyStorage(GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_LEFT_ALT);
    public static IBridge.InternalBridge internal = new InternalBridgeImplementation();
    public static IBridge.PreLaunchBridge preLaunchBridge = new IBridge.PreLaunchBridge() {
        @Override
        public MinecraftVersion getVersion() {
            return new MinecraftVersion(
                    MinecraftVersion.Version.COMBAT_TEST_8C,
                    "https://launcher.mojang.com/v1/objects/177472ace3ff5d98fbd63b4bcd5bbef5b035a018/client.jar",
                    "https://raw.githubusercontent.com/rizecookey/intermediary/master/mappings/1.16_combat-6.tiny",
                    "https://github.com/Ascend-Client/Ascend-Client/releases/download/Mappings/1.16_combat-6.tiny",
                    "https://launcher.mojang.com/v1/objects/5ea38a7b8d58837c97214f2a46e5e12151d51f83/client.txt"
            );
        }

        @Override
        public void info(String s) {
            System.out.println(s);
        }

        @Override
        public void error(String s) {
            System.err.println(s);
        }

        @Override
        public void error(Exception e) {
            e.printStackTrace(System.err);
        }

        @Override
        public List<File> getVersionMods() {
            //Enforce sha256 hashes on coded's GitHub
            String fapiHash = "32d1966e96bcc1f20fd875bd6d76b2e3a28461f328303ead7f0cdbb9d1f5106d";
            String cbbHash = "e7a57807f41926fcdc4989879882d635ff96500845d0df5d5847848ab5377202";

            ArrayList<File> list = new ArrayList<>();

            try {
                //ReplayMod compatible sodium
                list.add(Util.downloadIfFirstLaunch("https://www.replaymod.com/download/sodium-fabric-mc1.16.5-0.2.0+rev.f42b4ca.jar"));
                //Updated fabric api (by NotCoded)
                list.add(Util.downloadIfFirstLaunch("https://github.com/not-coded/fabric/releases/download/0.42.0%2B1.16.combat/fabric-api-0.42.0+1.16.combat.jar", fapiHash));
                //Sodium extras
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/PtjYWJkn/versions/Et3PybAh/sodium-extra-0.4.18%2Bmc1.16.5-build.96.jar"));
                //Reeses sodium extras
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/Bh37bMuy/versions/Em4mC86n/reeses_sodium_options-1.6.3%2Bmc1.16.5-build.86.jar"));
                //Iris
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/YL57xq9U/versions/1turazSM/iris-mc1.16.5-1.4.5.jar"));
                //Bedrock bridge
                list.add(Util.downloadIfFirstLaunch("https://github.com/betterclient/cts-8a-parity/releases/download/1.0.3/cts-8a-parity-1.0.2.jar", cbbHash));
                //LazyDFU
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/hvFnDODi/versions/0.1.2/lazydfu-0.1.2.jar"));
            } catch (Exception e) {
                IBridge.getPreLaunch().error(e);
            }
            return list;
        }

        @Override
        public void registerVersionAscendMods(ModuleManager manager) {
            manager.addModule(new CookeyMod());
        }

        @Override
        public void modifyVersion(ClassNode node, File mod) {
            modifyInternal(node);
        }
    };

    private static void modifyInternal(ClassNode node) {
        if(node.name.equals("com/replaymod/core/versions/MCVer")) {
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

        if(node.name.equals("net/coderbot/iris/gui/screen/ShaderPackScreen")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals(Application.isDev ? "init" : "method_25426")) {
                    List<AbstractInsnNode> toRemove = getRemovalNodes(method);

                    toRemove.forEach(method.instructions::remove);
                }
            }
        }

        if (node.name.equals("net/coderbot/iris/mixin/MixinMinecraft_NoAuthInDev")) {
            node.methods.removeIf(methodNode -> methodNode.name.equals("iris$noSocialInteractionsInDevelopment"));
        }

        if(node.name.equals("org/dimdev/vanillafix/profiler/mixins/client/KeyboardMixin")) {
            node.methods.removeIf(methodNode -> methodNode.name.equals("addF3SHelpMessage"));
        }

        //CTS Input.tick has 2 booleans instead of 1
        if(ModRemapperUtility.detectMixin(node)) {
            String mixinTarget = ModRemapperUtility.getDetectMixin(node);

            String mixinTarget0 = "L" + (Application.isDev ? "net/minecraft/client/input/Input" : "net/minecraft/class_744") + ";";
            String mixinTarget1 = "L" + (Application.isDev ? "net/minecraft/client/input/KeyboardInput" : "net/minecraft/class_743") + ";";

            if(!mixinTarget.equals(mixinTarget1) && !mixinTarget.equals(mixinTarget0)) return;
            for (MethodNode method : node.methods) {
                String mixinMethodTarget = ModRemapperUtility.getMixinTarget(method);

                if(mixinMethodTarget.equals("method_3129") || mixinMethodTarget.equals("tick")) {
                    method.desc = method.desc.replace("Z", "ZZ");

                    for (AbstractInsnNode instruction : method.instructions) {
                        if(instruction instanceof VarInsnNode vin && vin.var == 2 && vin.getOpcode() == ALOAD) vin.var = 3;
                    }
                }
            }
        }
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

    public static void setup() {
        bridge = new BridgeImpl();
    }
}