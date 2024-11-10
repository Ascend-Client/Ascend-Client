package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoDuplicates implements ClassTransformer {
    @Override
    public byte[] transform(String s, byte[] bytes) {
        BetterClassNode bcn = new BetterClassNode(bytes);

        List<Map.Entry<String, String>> seenMethods = new ArrayList<>();

        bcn.getOrigin().methods.removeIf(methodNode -> {
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(methodNode.name, methodNode.desc);
            if (seenMethods.contains(entry)) {
                return true;
            } else {
                seenMethods.add(entry);
                return false;
            }
        });

        return bcn.output();
    }
}