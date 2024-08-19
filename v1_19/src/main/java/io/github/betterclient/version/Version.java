package io.github.betterclient.version;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Version {
    private static final Logger LOGGER = LogManager.getLogger("Ballsack Client");

    public static IBridge bridge;
    public static IBridge.KeyStorage keys = new IBridge.KeyStorage(GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_LEFT_ALT);
    public static IBridge.InternalBridge internal = new InternalBridgeImplementation();
    public static IBridge.PreLaunchBridge preLaunchBridge = new IBridge.PreLaunchBridge() {
        @Override
        public MinecraftVersion getVersion() {
            return new MinecraftVersion(
                    MinecraftVersion.Version.V1_19_4,
                    "https://piston-data.mojang.com/v1/objects/958928a560c9167687bea0cefeb7375da1e552a8/client.jar",
                    "https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/1.19.4.tiny",
                    "https://github.com/betterclient/Minecraft-Client/releases/download/Mappings/1.19.4.tiny",
                    "https://piston-data.mojang.com/v1/objects/f14771b764f943c154d3a6fcb47694477e328148/client.txt"
            );
        }

        @Override
        public void info(String s) {
            LOGGER.info(s);
        }

        @Override
        public void error(String s) {
            LOGGER.error(s);
        }

        @Override
        public void error(Exception e) {
           LOGGER.error(e);
        }

        @Override
        public List<File> getVersionMods() {
            try {
                File fapi = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/P7dR8mSH/versions/nyAmoHlr/fabric-api-0.87.2%2B1.19.4.jar");
                File sodium = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/AANobbMI/versions/b4hTi3mo/sodium-fabric-mc1.19.4-0.4.10%2Bbuild.24.jar");
                File sodiumExtra = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/PtjYWJkn/versions/YknbqkHe/sodium-extra-0.4.18%2Bmc1.19.4-build.100.jar");
                File reesesSodiumExtras = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/Bh37bMuy/versions/vjKE54Zq/reeses_sodium_options-1.6.3%2Bmc1.19.4-build.90.jar");
                File iris = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/YL57xq9U/versions/wN6PuLPa/iris-mc1.19.4-1.6.11.jar");

                return List.of(fapi, iris, sodium, sodiumExtra, reesesSodiumExtras);
            } catch (IOException e) {
                IBridge.getPreLaunch().error(e);
            }
            return new ArrayList<>();
        }

        @Override
        public void registerVersionBallsackMods(ModuleManager manager) {
            //1.19 doesn't have any version specific mods atm
        }

        @Override
        public void modifyVersion(ClassNode node, File mod) throws IOException {
            if(node.name.equals("net/coderbot/iris/compat/sodium/mixin/vertex_format/entity/MixinEntityRenderDispatcher")) {
                for (MethodNode method : node.methods) {
                    if(method.name.equals("renderShadowPart")) {
                        method.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")));
                    }
                }
            }
        }
    };

    public static void setup() {
        bridge = new BridgeImpl();
    }
}
