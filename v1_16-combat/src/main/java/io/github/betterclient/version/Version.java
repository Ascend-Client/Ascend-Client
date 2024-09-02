package io.github.betterclient.version;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.mods.BedrockBridge;
import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
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
                    "https://github.com/betterclient/Minecraft-Client/releases/download/Mappings/1.16_combat-6.tiny",
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
            String cbbHash = "aa930499a33405461512ec02069b9ced7d0c398c7492ddb86d5a6cb1f4f20cee";

            ArrayList<File> list = new ArrayList<>();

            try {
                list.add(Util.downloadIfFirstLaunch("https://www.replaymod.com/download/sodium-fabric-mc1.16.5-0.2.0+rev.f42b4ca.jar"));
                list.add(Util.downloadIfFirstLaunch("https://github.com/not-coded/fabric/releases/download/0.42.0%2B1.16.combat/fabric-api-0.42.0+1.16.combat.jar", fapiHash));
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/PtjYWJkn/versions/Et3PybAh/sodium-extra-0.4.18%2Bmc1.16.5-build.96.jar"));
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/Bh37bMuy/versions/Em4mC86n/reeses_sodium_options-1.6.3%2Bmc1.16.5-build.86.jar"));
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/YL57xq9U/versions/1turazSM/iris-mc1.16.5-1.4.5.jar"));
                list.add(Util.downloadIfFirstLaunch("https://github.com/not-coded/cts-8a-parity/releases/download/1.0.2/cts-8a-parity-1.0.2.jar", cbbHash));
                list.add(Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/hvFnDODi/versions/0.1.2/lazydfu-0.1.2.jar"));
            } catch (Exception e) {
                IBridge.getPreLaunch().error(e);
            }
            return list;
        }

        @Override
        public void registerVersionAscendMods(ModuleManager manager) {
            manager.addModule(new CookeyMod());
            manager.addModule(new BedrockBridge());
        }

        @Override
        public void modifyVersion(ClassNode node, File mod) throws IOException {
            modifyInternal(node, mod);
        }
    };

    private static void modifyInternal(ClassNode node, File mod) throws IOException {
        if(node.name.equals("com/replaymod/core/versions/MCVer") && FabricLoader.getInstance().getModName(mod).equals("Replay Mod")) {
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

        if(node.name.equals("net/coderbot/iris/gui/screen/ShaderPackScreen") && FabricLoader.getInstance().getModName(mod).equals("Iris")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals(Application.isDev ? "init" : "method_25426")) {
                    List<AbstractInsnNode> toRemove = getRemovalNodes(method);

                    toRemove.forEach(method.instructions::remove);
                }
            }
        }

        if(node.name.equals("org/dimdev/vanillafix/profiler/mixins/client/KeyboardMixin")) {
            node.methods.removeIf(methodNode -> methodNode.name.equals("addF3SHelpMessage"));
        }

        //Not an issue
        if(node.name.equals("net/notcoded/cts8a_parity/CTS8aParity") && FabricLoader.getInstance().getModName(mod).equals("CTS 8a Parity")) {
            for (MethodNode method : node.methods) {
                if(method.name.equals("onInitializeClient")) {
                    boolean remove = false;
                    for (AbstractInsnNode instruction : method.instructions.toArray()) {
                        if(instruction instanceof InsnNode inode && inode.getOpcode() == POP) {
                            method.instructions.insert(inode, new InsnNode(RETURN));
                            remove = true;
                        }

                        if(remove) {
                            method.instructions.remove(instruction);
                        }
                    }
                } else if(method.name.equals("lambda$onInitializeClient$0")) {
                    AbstractInsnNode injectAfter = null;
                    for (AbstractInsnNode instruction : method.instructions.toArray()) {
                        if(instruction instanceof MethodInsnNode) {
                            injectAfter = instruction.getPrevious();
                            method.instructions.remove(instruction);
                        }

                        if(instruction instanceof FieldInsnNode || instruction.getOpcode() == ALOAD) {
                            method.instructions.remove(instruction);
                        }
                    }

                    InsnList injection = new InsnList();

                    injection.add(new MethodInsnNode(INVOKESTATIC, "io/github/betterclient/version/mods/BedrockBridge", "get", "()Lio/github/betterclient/version/mods/BedrockBridge;", false));
                    injection.add(new VarInsnNode(ALOAD, 2));
                    injection.add(new MethodInsnNode(INVOKEVIRTUAL, Application.isDev ? "net/minecraft/network/PacketByteBuf" : "net/minecraft/class_2540", "readBoolean", "()Z", false));
                    injection.add(new MethodInsnNode(INVOKEVIRTUAL, "io/github/betterclient/version/mods/BedrockBridge", "setServerAllowing", "(Z)V", false));

                    method.instructions.insert(injectAfter, injection);
                }
            }
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