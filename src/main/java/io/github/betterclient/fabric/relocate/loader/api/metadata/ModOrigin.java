package io.github.betterclient.fabric.relocate.loader.api.metadata;

import java.nio.file.Path;
import java.util.List;

public interface ModOrigin {
    Kind getKind();
    List<Path> getPaths();

    enum Kind {
        PATH, NESTED, UNKNOWN
    }
}
