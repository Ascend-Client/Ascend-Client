package io.github.betterclient.fabric.relocate.loader.api;

public interface MappingResolver {
    String mapClassName(String namespace, String className);
}
