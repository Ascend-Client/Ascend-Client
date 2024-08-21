package io.github.betterclient.fabric.relocate.loader.api.metadata.version;

import io.github.betterclient.fabric.FabricVersionParser;
import io.github.betterclient.fabric.relocate.loader.api.VersionParsingException;

public interface VersionPredicate {
    static VersionPredicate parse(String ver) throws VersionParsingException {
        return new FabricVersionParser(ver);
    }

    boolean test(Object ver) throws VersionParsingException;
}
