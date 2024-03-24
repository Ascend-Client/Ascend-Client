package io.github.betterclient.fabric.transformer;

import io.github.betterclient.client.asm.BetterClassNode;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveEntryPointImplements implements ClassTransformer {
    @Override
    public byte[] transform(String className, byte[] classFileBuffer) {
        if(classFileBuffer == null)
            return null;

        ClassReader  read = new ClassReader(classFileBuffer);
        List<String> matchedClasses = new ArrayList<>();
        boolean flag = Arrays.stream(read.getInterfaces()).noneMatch(s -> {
            if(s.contains("net/fabricmc/") && (s.contains("Initializer") || s.contains("Entrypoint"))) {
                matchedClasses.add(s);
                return true;
            }

            return false;
        });

        if(flag) {
            return classFileBuffer;
        }

        BetterClassNode classNode = new BetterClassNode(classFileBuffer);

        for (String matchedClass : matchedClasses) {
            classNode.getOrigin().interfaces.remove(matchedClass);
        }


        return classNode.output();
    }
}
