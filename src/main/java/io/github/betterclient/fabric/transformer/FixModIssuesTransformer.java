package io.github.betterclient.fabric.transformer;

import io.github.betterclient.client.asm.BetterClassNode;
import io.github.betterclient.client.util.modremapper.utility.ModIssueFixer;
import io.github.betterclient.quixotic.ClassTransformer;

public class FixModIssuesTransformer implements ClassTransformer {
    @Override
    public byte[] transform(String className, byte[] unTransformedClass) {
        if (
                className.startsWith("io.github.betterclient.client") ||
                        className.startsWith("io.github.betterclient.fabric") ||
                        className.startsWith("net.fabricmc.tinyremapper.") ||
                        className.startsWith("net.fabricmc.mappingio.") ||
                        unTransformedClass == null)
            return unTransformedClass;

        BetterClassNode bnode = new BetterClassNode(unTransformedClass);

        try {
            ModIssueFixer.edit(bnode.getOrigin(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bnode.output();
    }
}
