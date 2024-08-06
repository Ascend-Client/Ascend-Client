package io.github.betterclient.fabric.relocate.loader.api.metadata;

import io.github.betterclient.fabric.relocate.loader.api.Version;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ModMetadata {
    default Version getVersion() {
        return version -> 0;
    }

    boolean containsCustomValue(String s);
    CustomValue getCustomValue(String s);

    ModEnvironment getEnvironment();
    Collection<String> getProvides();
    String getDescription();

    ContactInformation getContact();

    default Collection<String> getLicense() {
        return List.of();
    }

    Collection<Person> getAuthors();
    Collection<Person> getContributors();
    Optional<String> getIconPath(int size);

    String getId();
    String getName();
    String getType();
}
