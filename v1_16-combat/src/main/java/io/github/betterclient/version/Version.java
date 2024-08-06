package io.github.betterclient.version;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.mods.BedrockBridge;
import io.github.betterclient.version.mods.CookeyMod;
import io.github.betterclient.version.util.InternalBridgeImplementation;
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
        public void registerVersionBallsackMods(ModuleManager manager) {
            manager.moduleList.add(new CookeyMod());
            manager.moduleList.add(new BedrockBridge());
        }
    };

    public static void setup() {
        bridge = new BridgeImpl();
    }
}