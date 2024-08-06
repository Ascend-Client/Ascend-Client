package io.github.betterclient.fabric.relocate;

import java.util.List;

public class RelocatedClasses {
    public static List<String> getFabricClasses() {
        return List.of(
                "net/fabricmc/api/EnvType",
                "net/fabricmc/api/ClientModInitializer",
                "net/fabricmc/api/DedicatedServerModInitializer",
                "net/fabricmc/loader/api/FabricLoader",
                "net/fabricmc/loader/api/ObjectShare",
                "net/fabricmc/loader/api/MappingResolver",
                "net/fabricmc/loader/api/ModContainer",
                "net/fabricmc/loader/api/SemanticVersion",
                "net/fabricmc/loader/api/Version",
                "net/fabricmc/loader/api/VersionParsingException",
                "net/fabricmc/loader/api/metadata/ModMetadata",
                "net/fabricmc/loader/api/metadata/ModOrigin",
                "net/fabricmc/loader/api/metadata/ModOrigin$Kind",
                "net/fabricmc/loader/api/metadata/CustomValue",
                "net/fabricmc/loader/api/metadata/ContactInformation",
                "net/fabricmc/loader/api/metadata/Person",
                "net/fabricmc/loader/api/metadata/CustomValue$CvObject",
                "net/fabricmc/loader/api/metadata/CustomValue$CvArray",
                "net/fabricmc/loader/api/metadata/CustomValue$CvType",
                "net/fabricmc/loader/api/metadata/ModEnvironment",
                "net/fabricmc/loader/api/entrypoint/EntrypointContainer"
        );
    }
}
