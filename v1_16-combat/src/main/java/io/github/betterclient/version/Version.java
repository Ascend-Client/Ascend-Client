package io.github.betterclient.version;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.mods.BedrockBridge;
import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Version {

    public static IBridge bridge;
    public static IBridge.KeyStorage keys = new IBridge.KeyStorage(GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_LEFT_ALT);
    public static IBridge.InternalBridge internal = new InternalBridgeImplementation();
    public static IBridge.PreLaunchBridge preLaunchBridge = new IBridge.PreLaunchBridge() {
        @Override
        public MinecraftVersion getVersion() {
            return new MinecraftVersion(
                    "1.16-combat-6",
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
        public List<File> getVersionMods() {
            try {
                File sodium = Util.downloadIfFirstLaunch("https://www.replaymod.com/download/sodium-fabric-mc1.16.5-0.2.0+rev.f42b4ca.jar");
                File fapi = Util.downloadIfFirstLaunch("https://github.com/rizecookey/fabric/releases/download/0.25.0%2B1.16.combat/fabric-api-0.25.0+1.16.combat.jar");
                File sodiumExtras = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/PtjYWJkn/versions/Et3PybAh/sodium-extra-0.4.18%2Bmc1.16.5-build.96.jar");
                File reesesSodiumExtras = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/Bh37bMuy/versions/Em4mC86n/reeses_sodium_options-1.6.3%2Bmc1.16.5-build.86.jar");
                File iris = Util.downloadIfFirstLaunch("https://cdn.modrinth.com/data/YL57xq9U/versions/1turazSM/iris-mc1.16.5-1.4.5.jar");

                return List.of(fapi, sodium, sodiumExtras, reesesSodiumExtras, iris);
            } catch (Exception e) {
                IBridge.getPreLaunch().error(e.toString());
            }
            return new ArrayList<>();
        }

        @Override
        public void registerVersionBallsackMods(ModuleManager manager) {
            manager.moduleList.add(new CookeyMod());
            manager.moduleList.add(new BedrockBridge());
        }
    };

    public static void setup() {
        bridge = new BridgeImpl();
    }
}