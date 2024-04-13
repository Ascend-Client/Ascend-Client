package io.github.betterclient.client.launch;

import io.github.betterclient.quixotic.Quixotic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> launchArgs = new ArrayList<>(Arrays.asList(args));
        launchArgs.add("--quixoticapp");
        launchArgs.add("io.github.betterclient.version.VersionApplication");
        Quixotic.main(launchArgs.toArray(new String[0]));
    }
}
