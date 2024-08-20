package io.github.betterclient.fabric.relocate.loader.api;

public class VersionParsingException extends Exception {
    public VersionParsingException(String yes) {
        super(yes);
    }

    public VersionParsingException() {
        super();
    }

    public VersionParsingException(String s, Exception e) {
        super(s, e);
    }
}
