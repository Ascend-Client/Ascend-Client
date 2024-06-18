package io.github.betterclient.fabric.accesswidener;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.accesswidener.AccessWidener;
import io.github.betterclient.fabric.accesswidener.AccessWidenerFieldOrMethod;
import io.github.betterclient.quixotic.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class AccessWidenerApplier implements ClassTransformer {
    public String accessWidenerPath;
    public AccessWidener parsed;
    public List<String> allClasses = new ArrayList<>();

    public AccessWidenerApplier(String accessWidenerPath) {
        this.accessWidenerPath = accessWidenerPath;
        InputStream is = FabricLoader.class.getResourceAsStream("/" + accessWidenerPath);
        if(is == null) throw new NullPointerException("Access Widener is null (?)");

        try {
            this.parsed = new AccessWidener(new String(is.readAllBytes()));
            is.close();
        } catch (Exception e) {
            IBridge.getPreLaunch().error(e.toString());
        }

        allClasses.addAll(this.parsed.classes);
        this.parsed.fields.forEach(accessWidenerFieldOrMethod -> allClasses.add(accessWidenerFieldOrMethod.containerClass()));
        this.parsed.methods.forEach(accessWidenerFieldOrMethod -> allClasses.add(accessWidenerFieldOrMethod.containerClass()));

    }


    @Override
    public byte[] transform(String s, byte[] bytes) {
        if(!allClasses.contains(s))
            return bytes;
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        if(this.parsed.classes.contains(s)) {
            node.access = fixAccess(node.access);
        }

        for (AccessWidenerFieldOrMethod method : this.parsed.methods) {
            if(method.containerClass().equals(s)) {
                for (MethodNode methodNode : node.methods) {
                    if(methodNode.name.equals(method.name()) && methodNode.desc.equals(method.desc())) {
                        methodNode.access = fixAccess(methodNode.access);
                    }
                }
            }
        }

        for (AccessWidenerFieldOrMethod field : this.parsed.fields) {
            if(field.containerClass().equals(s)) {
                for (FieldNode fieldNode : node.fields) {
                    if(fieldNode.name.equals(field.name()) && fieldNode.desc.equals(field.desc())) {
                        fieldNode.access = fixAccess(fieldNode.access);
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    private int fixAccess(int access) {
        if((access & 7) != ACC_PROTECTED) { //protected
            return (access & ~7) | Opcodes.ACC_PUBLIC; //public
        }

        if((access & 7) != Opcodes.ACC_PRIVATE) { //private
            return (access & ~7) | Opcodes.ACC_PUBLIC; //public
        }

        return access;
    }
}
