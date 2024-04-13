package io.github.betterclient.client.util.downloader;

import java.io.File;

public record DownloadedMinecraft(File intermediaryJar, File yarnJar, File intermediaryToYarn, MinecraftVersion version) {
}
