package io.github.betterclient.fabric.relocate.loader.api;

public interface MappingResolver {
    String mapClassName(String namespace, String className);
    String mapFieldName(String namespace, String owner, String name, String desc);
    String mapMethodName(String namespace, String owner, String name, String desc);
}
