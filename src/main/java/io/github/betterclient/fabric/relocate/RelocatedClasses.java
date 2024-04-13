package io.github.betterclient.fabric.relocate;

import java.util.List;

public class RelocatedClasses {
    public static List<String> getFabricClasses() {
        return List.of(
                "net/fabricmc/api/EnvType",
                "net/fabricmc/loader/api/FabricLoader",
                "net/fabricmc/loader/api/MappingResolver",
                "net/fabricmc/loader/api/ModContainer",
                "net/fabricmc/loader/api/SemanticVersion",
                "net/fabricmc/loader/api/Version",
                "net/fabricmc/loader/api/VersionParsingException",
                "net/fabricmc/loader/api/metadata/ModMetadata",
                "net/fabricmc/loader/api/metadata/ModOrigin",
                "net/fabricmc/loader/api/metadata/ModOrigin$Kind"
        );
    }
}
