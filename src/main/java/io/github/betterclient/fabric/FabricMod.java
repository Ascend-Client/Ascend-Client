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

public interface FabricMod {
    String name();

    List<String> clientEntries();
    List<String> preMainEntries();
    List<String> mainEntries();
    Map<String, String> allEntries();
    Map<String, CustomValue> customValues();

    List<String> mixinConfigs();

    String accessWidener();
    File from();
    String getContainer();
    void setContainer(String s);
    String version();
    String id();

    ModEnvironment environment();

    String description();

    ContactInformation getContact();

    Collection<Person> contributors();

    Collection<Person> authors();

    IconMap getIconMap();
}
