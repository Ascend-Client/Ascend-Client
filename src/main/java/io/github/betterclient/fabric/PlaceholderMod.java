package io.github.betterclient.fabric;

import io.github.betterclient.fabric.api.IconMap;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ContactInformation;
import io.github.betterclient.fabric.relocate.loader.api.metadata.CustomValue;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ModEnvironment;
import io.github.betterclient.fabric.relocate.loader.api.metadata.Person;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record PlaceholderMod(String description, String id, String version, String name, File from) implements FabricMod {
    @Override
    public List<String> clientEntries() {
        return List.of();
    }

    @Override
    public List<String> preMainEntries() {
        return List.of();
    }

    @Override
    public List<String> mainEntries() {
        return List.of();
    }

    @Override
    public Map<String, String> allEntries() {
        return Map.of();
    }

    @Override
    public Map<String, CustomValue> customValues() {
        return Map.of();
    }

    @Override
    public List<String> mixinConfigs() {
        return List.of();
    }

    @Override
    public String accessWidener() {
        return "";
    }

    @Override
    public String getContainer() {
        return null;
    }

    @Override
    public void setContainer(String s) {

    }

    @Override
    public ModEnvironment environment() {
        return ModEnvironment.CLIENT;
    }

    @Override
    public ContactInformation getContact() {
        return new ContactInformation() {
            @Override
            public Optional<String> get(String key) {
                return Optional.empty();
            }

            @Override
            public Map<String, String> asMap() {
                return Map.of();
            }
        };
    }

    @Override
    public Collection<Person> contributors() {
        return List.of();
    }

    @Override
    public Collection<Person> authors() {
        return List.of();
    }

    @Override
    public IconMap getIconMap() {
        return null;
    }
}
