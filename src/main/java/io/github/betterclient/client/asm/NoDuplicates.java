package io.github.betterclient.client.asm;

import io.github.betterclient.quixotic.ClassTransformer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoDuplicates implements ClassTransformer {
    @Override
    public byte[] transform(String name, byte[] basicClass) {
        if((!name.startsWith("net.minecraft") && !name.startsWith("com.mojang.blaze3d.")) || basicClass == null)
            return basicClass;

        BetterClassNode bcn = new BetterClassNode(basicClass);

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