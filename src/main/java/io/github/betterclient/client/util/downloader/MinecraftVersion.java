package io.github.betterclient.client.util.downloader;

public record MinecraftVersion(Version version, String clientJar, String intermediaryTiny, String yarnTiny, String clientTxt) {
    public enum Version {
        COMBAT_TEST_8C("1.16-combat-6", "1.16.3-combat.8.c"),
        V1_19_4("1.19.4"),
        V1_20_1("1.20.1"),
        V1_20_6("1.20.6"),
        V1_21_1("1.21.1");

        final String good;
        final String real;
        Version(String good) { this(good, good); }
        Version(String good, String real) {
            this.good = good;
            this.real = real;
        }

        public String goodName() {
            return good;
        }

        public String realName() {
            return real;
        }
    }
}
