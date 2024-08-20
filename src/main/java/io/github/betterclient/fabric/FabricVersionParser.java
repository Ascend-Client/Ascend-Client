package io.github.betterclient.fabric;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.fabric.relocate.loader.api.FabricLoader;
import io.github.betterclient.fabric.relocate.loader.api.SemanticVersion;
import io.github.betterclient.fabric.relocate.loader.api.VersionParsingException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static io.github.betterclient.fabric.FabricVersionParser.LoadingError.*;

public class FabricVersionParser {
    /**
    Check if mod is missing a dependency
     @param mod The mod to check
     */
    public static LoadingError checkIncompatible(File mod) {
        try {
            JarFile file = new JarFile(mod);

            JarEntry entry = file.getJarEntry("fabric.mod.json");
            if(entry == null) return NONE;

            {
                InputStream is = file.getInputStream(entry);
                String src = new String(is.readAllBytes());
                is.close();

                JSONObject obj = new JSONObject(src);

                if(obj.has("depends")) {
                    JSONObject as = obj.getJSONObject("depends");
                    for (String s : as.keySet()) {
                        if((!s.equals("fabricloader") && !s.equals("fabric-loader") && !s.equals("java"))) {
                            if (FabricLoader.instance.isModLoaded(s)) {
                                Object a = as.get(s);
                                String loadedVersion = FabricLoader.getInstance().getModContainer(s).orElseThrow().getMetadata().getVersion().getFriendlyString();

                                boolean isBroken = false;
                                if(a instanceof JSONArray arr) {
                                    for (String string : arr.toList().stream().map(String.class::cast).toList()) {
                                        if (!checkDep(string, loadedVersion)) {
                                            isBroken = false;
                                            break;
                                        } else {
                                            isBroken = true;
                                        }
                                    }
                                } else if(a instanceof String str) {
                                    if (checkDep(str, loadedVersion)) {
                                        isBroken = true;
                                    }
                                }

                                if (isBroken) {
                                    IBridge.getPreLaunch().error(s + " is not the correct version");
                                    return WRONG_VERSION;
                                }
                            } else {
                                IBridge.getPreLaunch().error(s + " is not loaded");
                                return MISSING_DEP;
                            }
                        }
                    }
                }

                if(obj.has("breaks")) {
                    JSONObject as = obj.getJSONObject("breaks");
                    for (String s : as.keySet()) {
                        String unwantedVersion = as.getString(s);
                        if(FabricLoader.instance.isModLoaded(s)) {
                            String loadedVersion = FabricLoader.getInstance().getModContainer(s).orElseThrow().getMetadata().getVersion().getFriendlyString();

                            if(!checkBreak(unwantedVersion, loadedVersion)) {
                                IBridge.getPreLaunch().error(s + " is loaded (" + loadedVersion + " : " + unwantedVersion + ")");
                                return BREAKS_LOADED;
                            }
                        }
                    }
                }
            }

            file.close();
            return NONE;
        } catch (IOException | VersionParsingException e) {
            return NONE;
        }
    }

    private static boolean checkBreak(String unwantedVersion, String loadedVersion) throws VersionParsingException {
        if(unwantedVersion.equals("*")) return false;

        FabricVersionParser loaded = new FabricVersionParser(loadedVersion);
        FabricVersionParser unwanted = new FabricVersionParser(unwantedVersion);
        int out = loaded.compareTo(unwanted);

        return out == -1;
    }

    private static boolean checkDep(String wantedVersion, String loadedVersion) throws VersionParsingException {
        if(wantedVersion.equals("*")) return false;
        if(wantedVersion.equals("1.16.5") && Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C) return false;

        FabricVersionParser loaded = new FabricVersionParser(loadedVersion);
        FabricVersionParser unwanted = new FabricVersionParser(wantedVersion);
        int out = loaded.compareTo(unwanted);

        if(out == -1)
            IBridge.getPreLaunch().error("Mod wants: \"" + wantedVersion + "\" but found: \"" + loadedVersion + "\"");
        return out == -1;
    }

    public enum LoadingError {
        NONE(""),
        MISSING_DEP("Mod is missing a dependency."),
        BREAKS_LOADED("Mod breaks other mod."),
        WRONG_VERSION("Dependency loaded but wrong version.");

        final String err;
        LoadingError(String err) {
            this.err = err;
        }

        public String getError() {
            return err;
        }
    }

    private static final Pattern DOT_SEPARATED_ID = Pattern.compile("|[-0-9A-Za-z]+(\\.[-0-9A-Za-z]+)*");
    private static final Pattern UNSIGNED_INTEGER = Pattern.compile("0|[1-9][0-9]*");
    private final String prerelease;
    private final int[] components;

    public FabricVersionParser(String version) throws VersionParsingException {
        int buildDelimPos = version.indexOf('+');

        if (buildDelimPos >= 0) {
            version = version.substring(0, buildDelimPos);
        }

        int dashDelimPos = version.indexOf('-');

        if (dashDelimPos >= 0) {
            prerelease = version.substring(dashDelimPos + 1);
            version = version.substring(0, dashDelimPos);
        } else {
            prerelease = null;
        }

        if (prerelease != null && !DOT_SEPARATED_ID.matcher(prerelease).matches()) {
            throw new VersionParsingException("Invalid prerelease string '" + prerelease + "'!");
        }

        if (version.endsWith(".")) {
            throw new VersionParsingException("Negative version number component found!");
        } else if (version.startsWith(".")) {
            throw new VersionParsingException("Missing version component!");
        }

        String[] componentStrings = version.split("\\.");

        if (componentStrings.length < 1) {
            throw new VersionParsingException("Did not provide version numbers!");
        }

        int[] components = new int[componentStrings.length];
        int firstWildcardIdx = -1;

        for (int i = 0; i < componentStrings.length; i++) {
            String compStr = componentStrings[i];

            if (compStr.equals("x") || compStr.equals("X") || compStr.equals("*")) {
                if (prerelease != null) {
                    throw new VersionParsingException("Pre-release versions are not allowed to use X-ranges!");
                }

                components[i] = SemanticVersion.COMPONENT_WILDCARD;
                if (firstWildcardIdx < 0) firstWildcardIdx = i;
                continue;
            } else if (i > 0 && components[i - 1] == SemanticVersion.COMPONENT_WILDCARD) {
                throw new VersionParsingException("Interjacent wildcard (1.x.2) are disallowed!");
            }

            if (compStr.trim().isEmpty()) {
                throw new VersionParsingException("Missing version number component!");
            }

            try {
                components[i] = Integer.parseInt(compStr);

                if (components[i] < 0) {
                    throw new VersionParsingException("Negative version number component '" + compStr + "'!");
                }
            } catch (NumberFormatException e) {
                throw new VersionParsingException("Could not parse version number component '" + compStr + "'!", e);
            }
        }

        // strip extra wildcards (1.x.x -> 1.x)
        if (firstWildcardIdx > 0 && components.length > firstWildcardIdx + 1) {
            components = Arrays.copyOf(components, firstWildcardIdx + 1);
        }

        this.components = components;
    }

    public int compareTo(FabricVersionParser o) {
        for (int i = 0; i < Math.max(getVersionComponentCount(), o.getVersionComponentCount()); i++) {
            int first = getVersionComponent(i);
            int second = o.getVersionComponent(i);

            if (first == SemanticVersion.COMPONENT_WILDCARD || second == SemanticVersion.COMPONENT_WILDCARD) {
                continue;
            }

            int compare = Integer.compare(first, second);
            if (compare != 0) return compare;
        }

        Optional<String> prereleaseA = getPrereleaseKey();
        Optional<String> prereleaseB = o.getPrereleaseKey();

        if (prereleaseA.isPresent() || prereleaseB.isPresent()) {
            if (prereleaseA.isPresent() && prereleaseB.isPresent()) {
                StringTokenizer prereleaseATokenizer = new StringTokenizer(prereleaseA.get(), ".");
                StringTokenizer prereleaseBTokenizer = new StringTokenizer(prereleaseB.get(), ".");

                while (prereleaseATokenizer.hasMoreElements()) {
                    if (prereleaseBTokenizer.hasMoreElements()) {
                        String partA = prereleaseATokenizer.nextToken();
                        String partB = prereleaseBTokenizer.nextToken();

                        if (UNSIGNED_INTEGER.matcher(partA).matches()) {
                            if (UNSIGNED_INTEGER.matcher(partB).matches()) {
                                int compare = Integer.compare(partA.length(), partB.length());
                                if (compare != 0) return compare;
                            } else {
                                return -1;
                            }
                        } else {
                            if (UNSIGNED_INTEGER.matcher(partB).matches()) {
                                return 1;
                            }
                        }

                        int compare = partA.compareTo(partB);
                        if (compare != 0) return compare;
                    } else {
                        return 1;
                    }
                }

                return prereleaseBTokenizer.hasMoreElements() ? -1 : 0;
            } else if (prereleaseA.isPresent()) {
                return o.hasWildcard() ? 0 : -1;
            } else { // prereleaseB.isPresent()
                return hasWildcard() ? 0 : 1;
            }
        } else {
            return 0;
        }
    }

    public boolean hasWildcard() {
        for (int i : components) {
            if (i < 0) {
                return true;
            }
        }

        return false;
    }

    public Optional<String> getPrereleaseKey() {
        return Optional.ofNullable(prerelease);
    }

    public int getVersionComponentCount() {
        return components.length;
    }

    public int getVersionComponent(int pos) {
        if (pos < 0) {
            throw new RuntimeException("Tried to access negative version number component!");
        } else if (pos >= components.length) {
            return components[components.length - 1] == SemanticVersion.COMPONENT_WILDCARD ? SemanticVersion.COMPONENT_WILDCARD : 0;
        } else {
            return components[pos];
        }
    }
}
