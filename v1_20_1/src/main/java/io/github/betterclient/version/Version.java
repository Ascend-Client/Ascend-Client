package io.github.betterclient.version;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Version {
    private static final Logger LOGGER = LogManager.getLogger("Ascend Client");

    public static IBridge bridge;
    public static IBridge.KeyStorage keys = new IBridge.KeyStorage(GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_LEFT_ALT);
    public static IBridge.InternalBridge internal;
    public static IBridge.PreLaunchBridge preLaunchBridge = new IBridge.PreLaunchBridge() {
        @Override
        public MinecraftVersion getVersion() {
            return new MinecraftVersion(
                    MinecraftVersion.Version.V1_20_1,
                    "https://piston-data.mojang.com/v1/objects/0c3ec587af28e5a785c0b4a7b8a30f9a8f78f838/client.jar",
                    "https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/1.20.1.tiny",
                    "https://github.com/betterclient/Minecraft-Client/releases/download/Mappings/1.20.1.tiny",
                    "https://piston-data.mojang.com/v1/objects/6c48521eed01fe2e8ecdadbd5ae348415f3c47da/client.txt"
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
                File fapi = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/P7dR8mSH/versions/P7uGFii0/fabric-api-0.92.2%2B1.20.1.jar");

                //1.20.1 lwjgl version is incompatible with sodium?????
                if (Boolean.getBoolean("fabric.development")) {
                    return List.of(fapi);
                }

                File sodium = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/AANobbMI/versions/ygf8cVZg/sodium-fabric-0.5.11%2Bmc1.20.1.jar");
                File sodiumExtra = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/PtjYWJkn/versions/I7ggF6B5/sodium-extra-0.5.4%2Bmc1.20.1-build.115.jar");
                File reesesSodiumExtras = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/Bh37bMuy/versions/Rc9pkPug/reeses_sodium_options-1.7.2%2Bmc1.20.1-build.101.jar");
                File iris = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/YL57xq9U/versions/1CMVXDHo/iris-1.7.2%2Bmc1.20.1.jar");

                return List.of(fapi, sodium, sodiumExtra, reesesSodiumExtras, iris);
            } catch (IOException e) {
                IBridge.getPreLaunch().error(e.toString());
            }
            return new ArrayList<>();
        }

        @Override
        public void registerVersionAscendMods(ModuleManager manager) {

        }

        @Override
        public void modifyVersion(ClassNode node, File mod) throws IOException {}
    };

    public static void setup() {
        internal = new InternalBridgeImplementation();
        bridge = new BridgeImpl();
    }
}
