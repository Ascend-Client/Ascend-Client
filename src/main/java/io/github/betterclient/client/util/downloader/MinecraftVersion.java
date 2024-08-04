package io.github.betterclient.client.util.downloader;

public record MinecraftVersion(Version version, String clientJar, String intermediaryTiny, String yarnTiny, String clientTxt) {
    public enum Version {
        COMBAT_TEST_8C,
        V1_19_4,
        V1_20_6
    }
}
